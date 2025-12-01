package ca.gbc.comp3095.apigateway.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String CLIENT_ID = "api-gateway";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/goals/**").hasRole("student")
                        .requestMatchers(HttpMethod.PUT, "/api/goals/**").hasRole("student")
                        .requestMatchers(HttpMethod.DELETE, "/api/goals/**").hasRole("student")
                        .requestMatchers(HttpMethod.PUT, "/api/events/*/register", "/api/events/*/unregister")
                        .hasRole("student")
                        .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("staff")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("staff")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("staff")
                        .requestMatchers(HttpMethod.POST, "/api/resources/**").hasRole("staff")
                        .requestMatchers(HttpMethod.PUT, "/api/resources/**").hasRole("staff")
                        .requestMatchers(HttpMethod.DELETE, "/api/resources/**").hasRole("staff")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(this::jwtAuthenticationConverter)))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    private JwtAuthenticationToken jwtAuthenticationConverter(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                        realmRoles(jwt).stream(),
                        clientRoles(jwt).stream())
                .toList();

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private Collection<GrantedAuthority> realmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return Collections.emptyList();
        }

        Object roles = realmAccess.get("roles");
        return convertRoles(roles);
    }

    private Collection<GrantedAuthority> clientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Collections.emptyList();
        }

        Object client = resourceAccess.get(CLIENT_ID);
        if (!(client instanceof Map<?, ?> clientMap)) {
            return Collections.emptyList();
        }

        return convertRoles(clientMap.get("roles"));
    }

    private Collection<GrantedAuthority> convertRoles(Object roles) {
        if (!(roles instanceof Collection<?> roleCollection)) {
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Object role : roleCollection) {
            if (Objects.isNull(role)) {
                continue;
            }
            String roleName = role.toString();
            if (!roleName.startsWith(ROLE_PREFIX)) {
                roleName = ROLE_PREFIX + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
        }

        return authorities;
    }
}
