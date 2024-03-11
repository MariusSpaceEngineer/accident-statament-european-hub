package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PolicyHolderPersonalInformationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName")
    @Mapping(target = "lastName")
    @Mapping(target = "address")
    @Mapping(target = "postalCode")
    @Mapping(target = "phoneNumber")
    @Mapping(target = "email")
    PolicyHolder updateFromDTO(PolicyHolderPersonalInformationDTO dto, @MappingTarget PolicyHolder entity);


    PolicyHolderPersonalInformationDTO toDTO(PolicyHolder policyHolder);
}
