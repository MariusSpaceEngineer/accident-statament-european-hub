package com.inetum.realdolmen.hubkitbackend.interfaces.services;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;

import java.util.List;
import java.util.Optional;

public interface IPolicyHolderService {
    Optional<PolicyHolderDTO> fetchPolicyHolderProfile(String token);

    Optional<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(String token, PolicyHolderPersonalInformationDTO policyHolderDTO);

    Optional<List<InsuranceCertificateDTO>> updateInsuranceCertificateInformation(String token, InsuranceCertificateDTO insuranceCertificateDTO) throws Exception;
}
