package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceAgencyDTO;
import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCompanyDTO;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceAgency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InsuranceAgencyMapper {
    @Mapping(target = "id", ignore = true)
    InsuranceAgency updateFromDTO(InsuranceAgencyDTO insuranceAgencyDTO, @MappingTarget InsuranceAgency insuranceAgency);

    @Mapping(target = "id", ignore = true)
    InsuranceAgency updateFromEntity(InsuranceAgency newInsuranceAgency, @MappingTarget InsuranceAgency insuranceAgency);

    @Mapping(target = "id", ignore = true)
    InsuranceAgency fromDTO(InsuranceAgencyDTO insuranceCompanyDTO);

}
