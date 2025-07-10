package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.util.CardFilter;
import com.example.bankcards.util.UserFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto save(String userName, String password, Set<String> roles);

    void deleteById(Long id);

    void deleteAll();

    UserDto update(Long id, String password, Set<String> roles);

    UserDto getById(Long id);

    List<UserDto> getAll(UserFilter filter, Pageable pageable);

    UserDto getByUsername(String username);

    void addRole(String roleName);
}
