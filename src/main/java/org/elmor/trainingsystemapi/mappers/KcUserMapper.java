package org.elmor.trainingsystemapi.mappers;

import org.elmor.trainingsystemapi.dtos.users.KcChangePasswordPostDto;
import org.elmor.trainingsystemapi.dtos.users.KcUpdateUserPutDto;
import org.elmor.trainingsystemapi.dtos.users.KcUserGetDto;
import org.elmor.trainingsystemapi.dtos.users.KcUserPostDto;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface KcUserMapper {

    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "emailVerified", constant = "true")
    @Mapping(target = "credentials", source = "password", qualifiedByName = "passwordToCredentialRepresentation")
    UserRepresentation toUserRepresentation(KcUserPostDto dto);

    KcUserGetDto toKcUserGetDto(UserRepresentation userRepresentation);

    @Mapping(target = "type", constant = "password")
    @Mapping(target = "temporary", constant = "false")
    @Mapping(target = "value", source = "newPassword")
    CredentialRepresentation toCredentialRepresentation(KcChangePasswordPostDto dto);

    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "emailVerified", constant = "true")
    UserRepresentation toUserRepresentation(KcUpdateUserPutDto dto);

    @Named("passwordToCredentialRepresentation")
    default List<CredentialRepresentation> passwordToCredentialRepresentation(String value) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType("password");
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(value);
        return Collections.singletonList(credentialRepresentation);
    }


}
