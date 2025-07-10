package com.example.bankcards.controller;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.UserFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class})
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /user/save - success")
    @WithMockUser(roles = "ADMIN")
    void saveUser_Success() throws Exception {
        UserDto user = new UserDto(1L, "john_doe", "password", Set.of("ADMIN"));
        when(userService.save(anyString(), anyString(), anySet())).thenReturn(user);
        String json = "{\"userName\":\"john_doe\",\"password\":\"password\",\"roles\":[\"ADMIN\"]}";
        mockMvc.perform(post("/user/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    @DisplayName("POST /user/save - invalid input")
    @WithMockUser(roles = "ADMIN")
    void saveUser_InvalidInput() throws Exception {
        String json = "{"; // некорректный JSON
        mockMvc.perform(post("/user/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /user/deleteById - success")
    @WithMockUser(roles = "ADMIN")
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/user/deleteById").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /user/deleteById - not found")
    @WithMockUser(roles = "ADMIN")
    void deleteById_NotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteById(999L);
        mockMvc.perform(delete("/user/deleteById").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /user/deleteAll - success")
    @WithMockUser(roles = "ADMIN")
    void deleteAll_Success() throws Exception {
        mockMvc.perform(delete("/user/deleteAll"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /user/deleteAll - forbidden for non-admin")
    @WithMockUser(roles = "USER")
    void deleteAll_Forbidden() throws Exception {
        mockMvc.perform(delete("/user/deleteAll"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /user/update - success")
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        UserDto user = new UserDto(1L, "john_doe", "newpassword", Set.of("ADMIN"));
        when(userService.update(anyLong(), anyString(), anySet())).thenReturn(user);
        String json = "{\"id\":1,\"password\":\"newpassword\",\"roles\":[\"ADMIN\"]}";
        mockMvc.perform(put("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    @Test
    @DisplayName("PUT /user/update - not found")
    @WithMockUser(roles = "ADMIN")
    void updateUser_NotFound() throws Exception {
        when(userService.update(anyLong(), anyString(), anySet())).thenThrow(new UserNotFoundException("User not found"));
        String json = "{\"id\":999,\"password\":\"newpassword\",\"roles\":[\"ADMIN\"]}";
        mockMvc.perform(put("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /user/getById - success")
    @WithMockUser(roles = "ADMIN")
    void getById_Success() throws Exception {
        UserDto user = new UserDto(1L, "john_doe", "password", Set.of("ADMIN"));
        when(userService.getById(1L)).thenReturn(user);
        mockMvc.perform(get("/user/getById").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    @DisplayName("GET /user/getById - not found")
    @WithMockUser(roles = "ADMIN")
    void getById_NotFound() throws Exception {
        when(userService.getById(999L)).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(get("/user/getById").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /user/getAll - success")
    @WithMockUser(roles = "ADMIN")
    void getAll_Success() throws Exception {
        UserDto user = new UserDto(1L, "john_doe", "password", Set.of("ADMIN"));
        when(userService.getAll(any(UserFilter.class), any())).thenReturn(List.of(user));
        mockMvc.perform(get("/user/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()));
    }

    @Test
    @DisplayName("GET /user/getAll - forbidden for non-admin")
    @WithMockUser(roles = "USER")
    void getAll_Forbidden() throws Exception {
        mockMvc.perform(get("/user/getAll"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /user/getByUserName - success")
    @WithMockUser(roles = "ADMIN")
    void getByUsername_Success() throws Exception {
        UserDto user = new UserDto(1L, "john_doe", "password", Set.of("ADMIN"));
        when(userService.getByUsername("john_doe")).thenReturn(user);
        mockMvc.perform(get("/user/getByUserName").param("username", "john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    @DisplayName("GET /user/getByUserName - not found")
    @WithMockUser(roles = "ADMIN")
    void getByUsername_NotFound() throws Exception {
        when(userService.getByUsername("not_found")).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(get("/user/getByUserName").param("username", "not_found"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /user/addRole - success")
    @WithMockUser(roles = "ADMIN")
    void addRole_Success() throws Exception {
        mockMvc.perform(post("/user/addRole").param("roleName", "MANAGER"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /user/addRole - forbidden for non-admin")
    @WithMockUser(roles = "USER")
    void addRole_Forbidden() throws Exception {
        mockMvc.perform(post("/user/addRole").param("roleName", "MANAGER"))
                .andExpect(status().isForbidden());
    }
}
