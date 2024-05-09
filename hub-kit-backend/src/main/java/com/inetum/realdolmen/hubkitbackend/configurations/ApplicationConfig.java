package com.inetum.realdolmen.hubkitbackend.configurations;

import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@Profile("!test")
public class ApplicationConfig {

    private final UserRepository repository;
    @Value("${mailjet.api.key}")
    private String mailjetApiKey;
    @Value("${mailjet.secret.key.api}")
    private String mailjetSecretKey;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvide = new DaoAuthenticationProvider();
        authProvide.setUserDetailsService(userDetailsService());
        authProvide.setPasswordEncoder(passwordEncoder());
        return authProvide;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MailjetClient mailjetClient(){
        ClientOptions options = ClientOptions.builder()
                .apiKey(mailjetApiKey)
                .apiSecretKey(mailjetSecretKey)
                .build();
        return new MailjetClient(options);
    }
}
