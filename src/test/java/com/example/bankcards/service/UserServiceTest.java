package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserFilter;
import com.example.bankcards.util.mappers.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("save - success")
    void save_Success() {
        User user = new User(); user.setId(1L); user.setUsername("john"); user.setPassword("password"); user.setRoles(Set.of(new Role("ADMIN")));
        UserDto userDto = new UserDto(1L, "john", "password", Set.of("ADMIN"));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByNameIn(Set.of("ADMIN"))).thenReturn(Set.of(new Role("ADMIN")));
        UserDto result = userService.save("john", "password", Set.of("ADMIN"));
        assertEquals(userDto, result);
    }

    @Test
    @DisplayName("save - user already exists")
    void save_UserAlreadyExists() {
        when(userRepository.save(any(User.class))).thenThrow(new EntityAlreadyExist("User exists"));
        assertThrows(EntityAlreadyExist.class, () -> userService.save("john", "password", Set.of("ADMIN")));
    }

    @Test
    @DisplayName("deleteById - success")
    void deleteById_Success() {
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(() -> userService.deleteById(1L));
    }

    @Test
    @DisplayName("deleteById - not found")
    void deleteById_NotFound() {
        doThrow(new UserNotFoundException("User not found")).when(userRepository).deleteById(999L);
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(999L));
    }

    @Test
    @DisplayName("deleteAll - success")
    void deleteAll_Success() {
        doNothing().when(userRepository).deleteAll();
        assertDoesNotThrow(() -> userService.deleteAll());
    }

    @Test
    @DisplayName("update - success")
    void update_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        UserDto userDto = new UserDto(1L, "john", "newpass", Set.of("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByNameIn(Set.of("ADMIN"))).thenReturn(Set.of(new Role("ADMIN")));
        when(passwordEncoder.encode("newpass")).thenReturn("newpass");
        UserDto result = userService.update(1L, "newpass", Set.of("ADMIN"));
        assertEquals(userDto, result);
    }

    @Test
    @DisplayName("update - user not found")
    void update_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.update(2L, "pass", Set.of("ADMIN")));
    }

    @Test
    @DisplayName("getById - success")
    void getById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setRoles(Set.of(new Role("ADMIN")));
        UserDto userDto = new UserDto(1L, "john", "password", Set.of("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getById(1L);
        assertEquals(userDto, result);
    }

    @Test
    @DisplayName("getById - not found")
    void getById_NotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getById(2L));
    }

    @Test
    @DisplayName("getAll - success")
    void getAll_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setRoles(Set.of(new Role("ADMIN")));
        UserDto userDto = new UserDto(1L, "john", "password", Set.of("ADMIN"));
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        List<UserDto> result = userService.getAll(new UserFilter(null, null), PageRequest.of(0, 10));
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    @DisplayName("getByUsername - success")
    void getByUsername_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setRoles(Set.of(new Role("ADMIN")));
        UserDto userDto = new UserDto(1L, "john", "password", Set.of("ADMIN"));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        UserDto result = userService.getByUsername("john");
        assertEquals(userDto, result);
    }

    @Test
    @DisplayName("getByUsername - not found")
    void getByUsername_NotFound() {
        when(userRepository.findByUsername("not_found")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getByUsername("not_found"));
    }

    @Test
    @DisplayName("addRole - success")
    void addRole_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        Role role = new Role();
        role.setName("MANAGER");
        when(roleRepository.existsRoleByName("MANAGER")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        assertDoesNotThrow(() -> userService.addRole("MANAGER"));
    }

    @Test
    @DisplayName("addRole - already exists")
    void addRole_AlreadyExists() {
        doThrow(new EntityAlreadyExist("Role exists")).when(roleRepository).save(any(Role.class));
        assertThrows(EntityAlreadyExist.class, () -> userService.addRole("MANAGER"));
    }
}
