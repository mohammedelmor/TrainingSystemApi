package org.elmor.trainingsystemapi.keycloak;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elmor.trainingsystemapi.config.KeycloakProperties;
import org.elmor.trainingsystemapi.dtos.users.*;
import org.elmor.trainingsystemapi.exception.*;
import org.elmor.trainingsystemapi.mappers.KcUserMapper;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class KcUserService {

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
    private final KcUserMapper mapper;

    public KcUserGetDto createNewUser(@Valid KcUserPostDto dto) {
        try {
            var userRepresentation = mapper.toUserRepresentation(dto);
            var response = keycloak.realm(keycloakProperties.getRealm())
                    .users()
                    .create(userRepresentation);
            validateResponse(response);
            String userId = CreatedResponseUtil.getCreatedId(response);
            var createdUser = keycloak.realm(keycloakProperties.getRealm())
                    .users()
                    .get(userId)
                    .toRepresentation();
            if (createdUser == null) {
                throw new KcUserNotFoundException("Couldn't find the created user");
            }
            return mapper.toKcUserGetDto(createdUser);
        } catch (KcBaseException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating user {}: {}", dto.username(), ex.getMessage(), ex);
            throw new KcUserServiceException(String.format("Failed to create the user '%s' due to an internal error.", dto.username()));
        }
    }

    public KcUserGetDto changePassword(@Valid KcChangePasswordPostDto dto) {
        try {
            if (dto.oldPassword().equals(dto.newPassword())) {
                throw new KcBaseException("new password must be different from old password");
            }
            var user = fetchUserByUsername(dto.username());
            validatePassword(dto.username(), dto.oldPassword());
            var newPassword = mapper.toCredentialRepresentation(dto);
            keycloak.realm(keycloakProperties.getRealm())
                    .users()
                    .get(user.getId())
                    .resetPassword(newPassword);
            return mapper.toKcUserGetDto(user);
        } catch (KcBaseException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred while changing password for user {}: {}", dto.username(), ex.getMessage(), ex);
            throw new KcUserServiceException("Failed to update the user password. Please try again later!");
        }
    }

    public KcUserGetDto updateUser(@Valid KcUpdateUserPutDto dto) {
        try {
            var user = fetchUserByUsername(dto.username());
            var userRepresentation = mapper.toUserRepresentation(dto);
            keycloak.realm(keycloakProperties.getRealm())
                    .users()
                    .get(user.getId())
                    .update(userRepresentation);
            return mapper.toKcUserGetDto(user);
        } catch (KcBaseException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred while updating user {}: {}", dto.username(), ex.getMessage(), ex);
            throw new KcUserServiceException("Failed to update the user. Please try again later!");
        }
    }

    public KcUserGetDto getUserByUsername(@NotNull(message = "username can't be null") String username) {
        try {
            var user = fetchUserByUsername(username);
            return mapper.toKcUserGetDto(user);
        } catch (KcBaseException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred while fetching user {}: {}", username, ex.getMessage(), ex);
            throw new KcUserServiceException("Failed to fetch the user. Please try again later!");
        }
    }

    private UserRepresentation fetchUserByUsername(String username) {
        var users = keycloak.realm(keycloakProperties.getRealm())
                .users()
                .search(username, true);
        if (users.isEmpty()) {
            throw new KcUserNotFoundException("User not found: " + username);
        }
        return users.get(0);
    }

    private void validateResponse(Response response) {
        if (response.getStatus() != HttpStatus.CREATED.value()) {
            String detailedMessage;
            try {
                detailedMessage = response.readEntity(KcResponseError.class).getErrorMessage();
            } catch (Exception e) {
                detailedMessage = response.getStatusInfo().getReasonPhrase();
            }
            log.error("Error occurred validating the response {}", detailedMessage);
            throw new KcUserCreationException(
                    "Couldn't create the user",
                    new Throwable(detailedMessage)
            );
        }
    }

    private void validatePassword(String username, String password) {
        try (var kc = KeycloakBuilder.builder()
                .realm(keycloakProperties.getRealm())
                .serverUrl(keycloakProperties.getHost())
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .build()) {
            var userAccessToken = kc.tokenManager().getAccessToken();
            if (userAccessToken == null) {
                throw new KcUserWrongPasswordException("Invalid Credentials");
            }
        } catch (KcBaseException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred validating the password for user {}: {}", username, ex.getMessage(), ex);
            throw new KcUserWrongPasswordException("Invalid Credentials");
        }

    }

}
