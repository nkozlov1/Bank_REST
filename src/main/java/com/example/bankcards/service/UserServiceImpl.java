package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityAlreadyExist;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserFilter;
import com.example.bankcards.util.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto save(String userName, String password, Set<String> roles) {
        if(userRepository.findByUsername(userName).isPresent()) {
            throw new EntityAlreadyExist("User with this name already exists");
        }
        Set<Role> userRoles = new HashSet<>(roleRepository.findByNameIn(roles));
        User user = new User(userName, passwordEncoder.encode(password), userRoles);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);

    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public UserDto update(Long id, String password, Set<String> roles) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id " + id + " not found")
        );
        if (!passwordEncoder.matches(password, user.getPassword()) & password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (roles != null) {
            Set<Role> userRoles = new HashSet<>(roleRepository.findByNameIn(roles));
            user.setRoles(userRoles);
        }
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDto).orElseThrow(
                () -> new UserNotFoundException("User with id " + id + "not found")
        );
    }

    public List<UserDto> getAll(UserFilter filter, Pageable pageable) {
        Page<User> users = userRepository.findAll(filter.toSpecification(), pageable);
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto getByUsername(String username) {
        return userRepository.findByUsername(username).map(UserMapper::toDto).orElseThrow(
                () -> new UserNotFoundException("User with username " + username + "not found")
        );
    }

    public void addRole(String roleName){
        if(roleRepository.existsRoleByName(roleName)){
            throw new EntityAlreadyExist("Role with name " + roleName + " already exists");
        }
        Role role = new Role(roleName);
        roleRepository.save(role);
    }
}
