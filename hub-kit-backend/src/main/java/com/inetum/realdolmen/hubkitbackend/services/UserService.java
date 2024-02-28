package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.UserProfileDTO;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JwtService jwtService;

    public Optional<UserProfileDTO> fetchUserProfile(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = repository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder userClass = (PolicyHolder) user.get();
                UserProfileDTO dto = UserProfileDTO.builder()
                        .firstName(userClass.getFirstName())
                        .lastName(userClass.getLastName())
                        .email(userClass.getEmail())
                        .build();

                return Optional.of(dto);
            } else {
                return Optional.empty(); // User not found
            }
        } catch (Exception e) {
            //TODO add exception handling
            return Optional.empty(); // Return empty Optional in case of exceptions
        }
    }

}
