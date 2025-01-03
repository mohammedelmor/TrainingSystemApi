package org.elmor.trainingsystemapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elmor.trainingsystemapi.dtos.users.KcChangePasswordPostDto;
import org.elmor.trainingsystemapi.dtos.users.KcUpdateUserPutDto;
import org.elmor.trainingsystemapi.dtos.users.KcUserGetDto;
import org.elmor.trainingsystemapi.dtos.users.KcUserPostDto;
import org.elmor.trainingsystemapi.keycloak.KcUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final KcUserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<KcUserGetDto> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/register")
    public ResponseEntity<KcUserGetDto> createUser(@Valid @RequestBody KcUserPostDto dto) {
        return ResponseEntity.ok(userService.createNewUser(dto));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<KcUserGetDto> changePassword(@Valid @RequestBody KcChangePasswordPostDto dto) {
        return ResponseEntity.ok(userService.changePassword(dto));
    }

    @PostMapping("/update")
    public ResponseEntity<KcUserGetDto> updateUser(@Valid @RequestBody KcUpdateUserPutDto dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }


}

