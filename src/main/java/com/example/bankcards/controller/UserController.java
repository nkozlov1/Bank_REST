package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.UserFilter;
import com.example.bankcards.util.requests.SaveUserRequest;
import com.example.bankcards.util.requests.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User management endpoints")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create new user", description = "Creates a new user. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> save(@Valid @RequestBody SaveUserRequest request) {
        return ResponseEntity.ok(userService.save(request.getUserName(), request.getPassword(), request.getRoles()));
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by ID. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted", content = @Content(schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/deleteById")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> deleteById(
            @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @Operation(summary = "Delete all users", description = "Deletes all users. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All users deleted", content = @Content)
    })
    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAll() {
        userService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update user", description = "Updates user data. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(request.getId(), request.getPassword(), request.getRoles()));
    }

    @Operation(summary = "Get user by ID", description = "Returns user by ID. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/getById")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getById(@Parameter(description = "User ID", required = true, example = "1") @RequestParam Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Get all users", description = "Returns all users with optional filtering. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users", content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAll(
            @Parameter(description = "User filter") @ModelAttribute UserFilter filter,
            @Parameter(description = "Page offset", example = "0") @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @Parameter(description = "Page limit", example = "20") @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return ResponseEntity.ok(userService.getAll(filter, PageRequest.of(offset, limit)));
    }

    @Operation(summary = "Get user by username", description = "Returns user by username. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/getByUserName")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getByUsername(
            @Parameter(description = "Username", required = true, example = "john_doe") @RequestParam String username) {
        return ResponseEntity.ok(userService.getByUsername(username));
    }

    @Operation(summary = "Add new role", description = "Adds a new role to the system. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role added", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/addRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addRole(
            @Parameter(description = "Role name", required = true, example = "MANAGER") @RequestParam String roleName) {
        userService.addRole(roleName);
        return ResponseEntity.ok().build();
    }
}