package ru.otus.hw.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/book/new", "/book/*/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/book").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/book/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/book/*").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/forbidden")
                );
        return http.build();
    }
}
