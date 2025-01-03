package org.elmor.trainingsystemapi.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record KcUserPostDto(
        @NotNull(message = "Username can't be null")
        String username,
        @NotNull(message = "Password can't be null")
        String password,
        @NotNull(message = "Email can't be null")
        @Email(message = "Not a valid email")
        String email,
        @NotNull(message = "First name can't be null")
        String firstName,
        @NotNull(message = "Last name can't be null")
        String lastName
) {
}
