package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceCertificate;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PolicyHolderMapper {
    PolicyHolder fromDTO(PolicyHolderDTO policyHolderDTO);

    PolicyHolderDTO toDTO(PolicyHolder policyHolder);

}
