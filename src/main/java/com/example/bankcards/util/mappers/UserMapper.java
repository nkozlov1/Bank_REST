package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {
    public UserDto toDto(User user) {
        Set<Role> rolesRole = user.getRoles();
        Set<String> rolesString = rolesRole.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), rolesString);
    }

    public User toDao(UserDto cardDto) {
        return new User(cardDto.getId(), cardDto.getUsername(), cardDto.getPassword());
    }
}
