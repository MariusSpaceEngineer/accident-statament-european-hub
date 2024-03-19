package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCompanyDTO;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceCompany;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InsuranceCompanyMapper {
    @Mapping(target = "id", ignore = true)
    InsuranceCompany updateFromDTO(InsuranceCompanyDTO insuranceCompanyDTO, @MappingTarget InsuranceCompany insuranceCompany);

    @Mapping(target = "id", ignore = true)
    InsuranceCompany fromDTO(InsuranceCompanyDTO insuranceCompanyDTO);

}
