package com.projekt.wypozyczalnia.configurations;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    private final String clientId;

    public KeycloakRealmRoleConverter(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Set<String> roles = new HashSet<>();
        roles.addAll(extractRealmRoles(source));
        roles.addAll(extractResourceRoles(source));

        if (roles.isEmpty()) {
            return Collections.emptySet();
        }

        return roles.stream()
                .filter(StringUtils::hasText)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Collection<String> extractRealmRoles(Jwt source) {
        Object realmAccess = source.getClaim(REALM_ACCESS);
        if (realmAccess instanceof Map<?, ?> realmMap) {
            Object roles = realmMap.get(ROLES);
            if (roles instanceof Collection<?> collection) {
                return collection.stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    private Collection<String> extractResourceRoles(Jwt source) {
        Object resourceAccess = source.getClaim(RESOURCE_ACCESS);
        if (resourceAccess instanceof Map<?, ?> resourceMap) {
            Object resource = resourceMap.get(clientId);
            if (resource instanceof Map<?, ?> clientMap) {
                Object roles = clientMap.get(ROLES);
                if (roles instanceof Collection<?> collection) {
                    return collection.stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet());
                }
            }
        }
        return Collections.emptySet();
    }
}
