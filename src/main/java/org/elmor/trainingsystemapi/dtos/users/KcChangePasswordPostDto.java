package org.elmor.trainingsystemapi.dtos.users;

import jakarta.validation.constraints.NotNull;

public record KcChangePasswordPostDto(
        @NotNull(message = "Username can't be null")
        String username,
        @NotNull(message = "Old password can't be null")
        String oldPassword,
        @NotNull(message = "New password can't be null")
        String newPassword
) {
}
