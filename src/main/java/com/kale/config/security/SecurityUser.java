package com.kale.config.security;

import com.kale.domain.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class SecurityUser extends org.springframework.security.core.userdetails.User {

    public SecurityUser(User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
    }
}