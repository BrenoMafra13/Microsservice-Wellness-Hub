package ca.gbc.comp3095.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").hasRole("admin")
                
                // Event endpoints - students can register, staff can manage
                .requestMatchers("POST", "/api/events").hasAnyRole("staff", "admin")
                .requestMatchers("PUT", "/api/events/{id}").hasAnyRole("staff", "admin")
                .requestMatchers("DELETE", "/api/events/{id}").hasAnyRole("staff", "admin")
                .requestMatchers("PUT", "/api/events/{id}/register").hasAnyRole("student", "admin")
                .requestMatchers("PUT", "/api/events/{id}/unregister").hasAnyRole("student", "admin")
                .requestMatchers("GET", "/api/events/**").authenticated()
                
                // Resource endpoints - staff can manage, all can view
                .requestMatchers("POST", "/api/resources").hasAnyRole("staff", "admin")
                .requestMatchers("PUT", "/api/resources/{id}").hasAnyRole("staff", "admin")
                .requestMatchers("DELETE", "/api/resources/{id}").hasAnyRole("staff", "admin")
                .requestMatchers("GET", "/api/resources/**").authenticated()
                
                // Goal endpoints - students can manage their own goals
                .requestMatchers("/api/goals/**").hasAnyRole("student", "admin")
                
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return converter;
    }

    @Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return jwt -> {
            // Extract realm roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            Collection<GrantedAuthority> realmRoles = List.of();
            
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                realmRoles = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
            }

            // Extract scope-based authorities
            JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);

            // Combine both
            return Stream.concat(realmRoles.stream(), scopeAuthorities.stream())
                .collect(Collectors.toList());
        };
    }
}
