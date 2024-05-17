package com.inetum.realdolmen.hubkitbackend.interfaces.controllers;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserController {
    ResponseEntity<PolicyHolderDTO> getPolicyHolderProfile(HttpServletRequest request);

    ResponseEntity<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(HttpServletRequest request, @Valid @RequestBody PolicyHolderPersonalInformationDTO policyHolderDTO);

    ResponseEntity<?> updatePolicyHolderInsuranceInformation(HttpServletRequest request, @RequestBody @Valid InsuranceCertificateDTO insuranceCertificateDTO);

}
