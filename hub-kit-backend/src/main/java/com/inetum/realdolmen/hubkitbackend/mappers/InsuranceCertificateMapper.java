package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceCertificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {InsuranceAgencyMapper.class, InsuranceCompanyMapper.class})
public interface InsuranceCertificateMapper {

    @Mapping(target = "vehicle", ignore = true)
    InsuranceCertificateDTO toDTO(InsuranceCertificate insuranceCertificate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "insuranceCompany.id", ignore = true)
    @Mapping(target = "insuranceAgency.id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    InsuranceCertificate updateFromDTO(InsuranceCertificateDTO insuranceCertificateDTO, @MappingTarget InsuranceCertificate insuranceCertificate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "insuranceCompany.id", ignore = true)
    @Mapping(target = "insuranceAgency.id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    InsuranceCertificate updateFromEntity(InsuranceCertificate newInsuranceCertificate, @MappingTarget InsuranceCertificate insuranceCertificate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "insuranceCompany.id", ignore = true)
    @Mapping(target = "insuranceAgency.id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    InsuranceCertificate fromDTO(InsuranceCertificateDTO insuranceCertificateDTO);
}




