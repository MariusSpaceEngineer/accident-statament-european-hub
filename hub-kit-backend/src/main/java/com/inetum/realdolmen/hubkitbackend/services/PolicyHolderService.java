package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceAgency;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceCompany;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceAgencyRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceCertificateRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceCompanyRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyHolderService {
    private final UserRepository userRepository;
    private final InsuranceCertificateRepository insuranceCertificateRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final InsuranceAgencyRepository insuranceAgencyRepository;
    private final JwtService jwtService;

    private final PolicyHolderMapper policyHolderMapper;
    private final InsuranceCertificateMapper insuranceCertificateMapper;
    private final InsuranceCompanyMapper insuranceCompanyMapper;
    private final InsuranceAgencyMapper insuranceAgencyMapper;
    private final PolicyHolderPersonalInformationMapper personalInformationMapper;

    public Optional<PolicyHolderDTO> fetchPolicyHolderProfile(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = userRepository.findByEmail(email);

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

    //TODO: send PolicyHolder all fields back

    public Optional<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(String token, PolicyHolderPersonalInformationDTO policyHolderDTO) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder policyHolder = (PolicyHolder) user.get();

                personalInformationMapper.updateFromDTO(policyHolderDTO, policyHolder);

                userRepository.save(policyHolder);

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
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                PolicyHolder existingPolicyHolder = (PolicyHolder) user.get();

                if (existingPolicyHolder.getInsuranceCertificate() == null) {
                    Optional<InsuranceCompany> existingInsuranceCompany = insuranceCompanyRepository.findByName(insuranceCertificateDTO.getInsuranceCompany().getName());
                    InsuranceCompany savedInsuranceCompany;
                    if (existingInsuranceCompany.isPresent()) {
                        var updatedInsuranceCompany = insuranceCompanyMapper.updateFromDTO(insuranceCertificateDTO.getInsuranceCompany(), existingInsuranceCompany.get());
                        savedInsuranceCompany = insuranceCompanyRepository.save(updatedInsuranceCompany);
                    } else {
                        var newInsuranceCompany = insuranceCompanyMapper.fromDTO(insuranceCertificateDTO.getInsuranceCompany());
                        savedInsuranceCompany = insuranceCompanyRepository.save(newInsuranceCompany);
                    }

                    Optional<InsuranceAgency> existingInsuranceAgency = insuranceAgencyRepository.findByNameAndAddress(insuranceCertificateDTO.getInsuranceAgency().getName(), insuranceCertificateDTO.getInsuranceAgency().getAddress());
                    InsuranceAgency savedInsuranceAgency;
                    if (existingInsuranceAgency.isPresent()) {
                        var updatedInsuranceAgency = insuranceAgencyMapper.updateFromDTO(insuranceCertificateDTO.getInsuranceAgency(), existingInsuranceAgency.get());
                        savedInsuranceAgency = insuranceAgencyRepository.save(updatedInsuranceAgency);
                    } else {
                        var newInsuranceAgency = insuranceAgencyMapper.fromDTO(insuranceCertificateDTO.getInsuranceAgency());
                        savedInsuranceAgency = insuranceAgencyRepository.save(newInsuranceAgency);
                    }

                    existingPolicyHolder.setInsuranceCertificate(insuranceCertificateMapper.fromDTO(insuranceCertificateDTO));
                    existingPolicyHolder.getInsuranceCertificate().setInsuranceCompany(savedInsuranceCompany);
                    existingPolicyHolder.getInsuranceCertificate().setInsuranceAgency(savedInsuranceAgency);

                } else {
                    existingPolicyHolder.setInsuranceCertificate(insuranceCertificateMapper.updateFromDTO(insuranceCertificateDTO, existingPolicyHolder.getInsuranceCertificate()));
                }

                insuranceCertificateRepository.save(existingPolicyHolder.getInsuranceCertificate());
                userRepository.save(existingPolicyHolder);
                InsuranceCertificateDTO result = insuranceCertificateMapper.toDTO(existingPolicyHolder.getInsuranceCertificate());

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
