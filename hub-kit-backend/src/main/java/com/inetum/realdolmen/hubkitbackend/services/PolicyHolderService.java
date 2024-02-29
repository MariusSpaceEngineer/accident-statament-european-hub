package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceAgencyDTO;
import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCompanyDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyHolderService {
    private final UserRepository repository;
    private final JwtService jwtService;

    public Optional<PolicyHolderDTO> fetchPolicyHolderProfile(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = repository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder policyHolder = (PolicyHolder) user.get();

                InsuranceCertificate insuranceCertificate = policyHolder.getInsuranceCertificate();

                InsuranceCompany insuranceCompany = insuranceCertificate.getInsuranceCompany();

                InsuranceCompanyDTO insuranceCompanyDTO = InsuranceCompanyDTO.builder()
                        .id(insuranceCompany.getId())
                        .name(insuranceCompany.getName())
                        .build();

                InsuranceAgency insuranceAgency = insuranceCertificate.getInsuranceAgency();

                InsuranceAgencyDTO insuranceAgencyDTO = InsuranceAgencyDTO.builder()
                        .id(insuranceAgency.getId())
                        .name(insuranceAgency.getName())
                        .address(insuranceAgency.getAddress())
                        .country(insuranceAgency.getCountry())
                        .phoneNumber(insuranceAgency.getPhoneNumber())
                        .email(insuranceAgency.getEmail())
                        .build();

                InsuranceCertificateDTO insuranceCertificateDTO = InsuranceCertificateDTO.builder()
                        .id(insuranceCertificate.getId())
                        .policyNumber(insuranceCertificate.getPolicyNumber())
                        .greenCardNumber(insuranceCertificate.getGreenCardNumber())
                        .availabilityDate(insuranceCertificate.getAvailabilityDate())
                        .expirationDate(insuranceCertificate.getExpirationDate())
                        .insuranceAgency(insuranceAgencyDTO)
                        .insuranceCompany(insuranceCompanyDTO)
                        .build();

                PolicyHolderDTO dto = PolicyHolderDTO.builder()
                        .id(policyHolder.getId())
                        .email(policyHolder.getEmail())
                        .firstName(policyHolder.getFirstName())
                        .lastName(policyHolder.getLastName())
                        .address(policyHolder.getAddress())
                        .postalCode(policyHolder.getPostalCode())
                        .phoneNumber(policyHolder.getPhoneNumber())
                        .insuranceCertificate(insuranceCertificateDTO)
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
