package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InsuranceCertificateMapper.class})
public interface PolicyHolderMapper {
    @Mapping(target = "id", ignore = true)
    PolicyHolder fromDTO(PolicyHolderDTO policyHolderDTO);

    PolicyHolderDTO toDTO(PolicyHolder policyHolder);

}
