package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceAgencyRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceCertificateRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceCompanyRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public Optional<PolicyHolderDTO> fetchPolicyHolderProfile(String token) throws Exception {
        Optional<User> user = getUser(token);

        if (user.isPresent()) {
            PolicyHolder policyHolder = (PolicyHolder) user.get();
            PolicyHolderDTO dto = policyHolderMapper.toDTO(policyHolder);

            return Optional.of(dto);
        } else {
            return Optional.empty(); // User not found
        }
    }

    public Optional<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(String token, PolicyHolderPersonalInformationDTO policyHolderDTO) throws Exception {
        Optional<User> user = getUser(token);

        if (user.isPresent()) {
            PolicyHolder policyHolder = (PolicyHolder) user.get();

            personalInformationMapper.updateFromDTO(policyHolderDTO, policyHolder);

            userRepository.save(policyHolder);

            return Optional.of(personalInformationMapper.toDTO(policyHolder));
        } else {
            return Optional.empty(); // User not found
        }
    }


    public Optional<List<InsuranceCertificateDTO>> updateInsuranceCertificateInformation(String token, InsuranceCertificateDTO insuranceCertificateDTO) throws Exception {

        Optional<User> user = getUser(token);

        if (user.isPresent()) {
            PolicyHolder existingPolicyHolder = (PolicyHolder) user.get();

            InsuranceCompany savedInsuranceCompany = getOrCreateInsuranceCompany(insuranceCertificateDTO);
            InsuranceAgency savedInsuranceAgency = getOrCreateInsuranceAgency(insuranceCertificateDTO);

            //If the policyHolder has no insurance certificate
            if (existingPolicyHolder.getInsuranceCertificates().isEmpty()) {
                addInsuranceCertificateToPolicyHolder(insuranceCertificateDTO, existingPolicyHolder, savedInsuranceCompany, savedInsuranceAgency);
            } else {
                updateInsuranceCertificateOfPolicyHolder(insuranceCertificateDTO, existingPolicyHolder, savedInsuranceCompany, savedInsuranceAgency);
            }

            insuranceCertificateRepository.saveAll(existingPolicyHolder.getInsuranceCertificates());
            userRepository.save(existingPolicyHolder);

            List<InsuranceCertificateDTO> result = new ArrayList<>();

            for (InsuranceCertificate insurance : existingPolicyHolder.getInsuranceCertificates()) {
                var insuranceDTO = insuranceCertificateMapper.toDTO(insurance);
                result.add(insuranceDTO);
            }

            return Optional.of(result);

        } else {
            // User not found
            return Optional.empty();
        }

    }

    private Optional<User> getUser(String token) {
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email);
    }

    private InsuranceCompany getOrCreateInsuranceCompany(InsuranceCertificateDTO insuranceCertificateDTO) {
        Optional<InsuranceCompany> existingInsuranceCompany = insuranceCompanyRepository.findByName(insuranceCertificateDTO.getInsuranceCompany().getName());
        if (existingInsuranceCompany.isPresent()) {
            var updatedInsuranceCompany = insuranceCompanyMapper.updateFromDTO(insuranceCertificateDTO.getInsuranceCompany(), existingInsuranceCompany.get());
            return insuranceCompanyRepository.save(updatedInsuranceCompany);
        } else {
            var newInsuranceCompany = insuranceCompanyMapper.fromDTO(insuranceCertificateDTO.getInsuranceCompany());
            return insuranceCompanyRepository.save(newInsuranceCompany);
        }
    }

    private InsuranceAgency getOrCreateInsuranceAgency(InsuranceCertificateDTO insuranceCertificateDTO) {
        Optional<InsuranceAgency> existingInsuranceAgency = insuranceAgencyRepository.findByNameAndAddress(insuranceCertificateDTO.getInsuranceAgency().getName(), insuranceCertificateDTO.getInsuranceAgency().getAddress());
        if (existingInsuranceAgency.isPresent()) {
            var updatedInsuranceAgency = insuranceAgencyMapper.updateFromDTO(insuranceCertificateDTO.getInsuranceAgency(), existingInsuranceAgency.get());
            return insuranceAgencyRepository.save(updatedInsuranceAgency);
        } else {
            var newInsuranceAgency = insuranceAgencyMapper.fromDTO(insuranceCertificateDTO.getInsuranceAgency());
            return insuranceAgencyRepository.save(newInsuranceAgency);
        }
    }

    private void updateInsuranceCertificateOfPolicyHolder(InsuranceCertificateDTO insuranceCertificateDTO, PolicyHolder existingPolicyHolder, InsuranceCompany savedInsuranceCompany, InsuranceAgency savedInsuranceAgency) {
        var insuranceCertificates = existingPolicyHolder.getInsuranceCertificates();
        var certificateToReplace = insuranceCertificates.stream()
                .filter(certificate -> Objects.equals(certificate.getId(), insuranceCertificateDTO.getId()))
                .findFirst();

        if (certificateToReplace.isPresent()) {
            var updatedCertificate = insuranceCertificateMapper.updateFromDTO(insuranceCertificateDTO, certificateToReplace.get());
            insuranceCertificates.remove(certificateToReplace.get());
            insuranceCertificates.add(updatedCertificate);
        } else {
            existingPolicyHolder.getInsuranceCertificates().add(insuranceCertificateMapper.fromDTO(insuranceCertificateDTO));
            existingPolicyHolder.getInsuranceCertificates().getLast().setInsuranceCompany(savedInsuranceCompany);
            existingPolicyHolder.getInsuranceCertificates().getLast().setInsuranceAgency(savedInsuranceAgency);
        }
    }

    private void addInsuranceCertificateToPolicyHolder(InsuranceCertificateDTO insuranceCertificateDTO, PolicyHolder existingPolicyHolder, InsuranceCompany savedInsuranceCompany, InsuranceAgency savedInsuranceAgency) {
        existingPolicyHolder.getInsuranceCertificates().add(insuranceCertificateMapper.fromDTO(insuranceCertificateDTO));
        existingPolicyHolder.getInsuranceCertificates().getFirst().setInsuranceCompany(savedInsuranceCompany);
        existingPolicyHolder.getInsuranceCertificates().getFirst().setInsuranceAgency(savedInsuranceAgency);
    }
}
