package com.inetum.realdolmen.hubkitbackend.utils;

import com.inetum.realdolmen.hubkitbackend.Roles;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository repository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedUsersTable();
    }

    private void seedUsersTable() {
        String sql = "SELECT * FROM users U";
        List<User> u = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if (u.isEmpty()) {

            var user = PolicyHolder.builder()
                    .firstName("John").
                    lastName("Doe").
                    email("johndoe@gmail.com")
                    .role(Roles.POLICY_HOLDER)
                    .password(new BCryptPasswordEncoder().encode("1234"))
                    .build();

            repository.save(user);

            log.info("Users Seeded");
        } else {
            log.info("Users Seeding Not Required");
        }
    }
}
