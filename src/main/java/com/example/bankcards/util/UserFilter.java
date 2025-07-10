package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

public record UserFilter(String username, List<String> roles) {
    public Specification<User> toSpecification() {
        return Specification.where(usernameSpec())
                .and(rolesSpec());
    }

    private Specification<User> usernameSpec() {
        return ((root, query, cb) -> StringUtils.hasText(username)
                ? cb.like(root.get("username"), "%" + username + "%")
                : null);

    }
    private Specification<User> rolesSpec() {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) {
                return null;
            }
            Join<User, Role> roleJoin = root.join("roles");
            query.distinct(true);
            return roleJoin.get("name").in(roles);
        };
    }
}
