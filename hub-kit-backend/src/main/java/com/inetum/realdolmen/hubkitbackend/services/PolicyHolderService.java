package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import com.inetum.realdolmen.hubkitbackend.mappers.InsuranceMapper;
import com.inetum.realdolmen.hubkitbackend.mappers.PolicyHolderMapper;
import com.inetum.realdolmen.hubkitbackend.mappers.PolicyHolderPersonalInformationMapper;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyHolderService {
    private final UserRepository repository;
    private final JwtService jwtService;

    private final PolicyHolderMapper policyHolderMapper;
    private final InsuranceMapper insuranceMapper;
    private final PolicyHolderPersonalInformationMapper personalInformationMapper;

    public Optional<PolicyHolderDTO> fetchPolicyHolderProfile(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = repository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder policyHolder = (PolicyHolder) user.get();
                PolicyHolderDTO dto = policyHolderMapper.toDTO(policyHolder);

                return Optional.of(dto);
            } else {
                return Optional.empty(); // User not found
            }
        } catch (Exception e) {
            //TODO add exception handling
            return Optional.empty(); // Return empty Optional in case of exceptions
        }
    }

    public Optional<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(String token, PolicyHolderPersonalInformationDTO policyHolderDTO) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = repository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder policyHolder = (PolicyHolder) user.get();

                personalInformationMapper.updateFromDTO(policyHolderDTO, policyHolder);

                repository.save(policyHolder);

                return Optional.of(personalInformationMapper.toDTO(policyHolder));

            } else {
                return Optional.empty(); // User not found
            }
        } catch (Exception e) {
            //TODO add exception handling
            return Optional.empty(); // Return empty Optional in case of exceptions
        }
    }

    public Optional<InsuranceCertificateDTO> updateInsuranceCertificateInformation(String token, InsuranceCertificateDTO insuranceCertificateDTO) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = repository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder policyHolder = (PolicyHolder) user.get();

                policyHolder.setInsuranceCertificate(insuranceMapper.updateFromDTO(insuranceCertificateDTO, policyHolder.getInsuranceCertificate()));

                repository.save(policyHolder);
                InsuranceCertificateDTO result = insuranceMapper.toDTO(policyHolder.getInsuranceCertificate());

                return Optional.of(result);

            } else {
                return Optional.empty(); // User not found
            }
        } catch (Exception e) {
            //TODO add exception handling
            return Optional.empty(); // Return empty Optional in case of exceptions
        }
    }

}
