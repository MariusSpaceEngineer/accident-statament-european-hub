package com.inetum.realdolmen.hubkitbackend.mappers;


import com.inetum.realdolmen.hubkitbackend.dto.TrailerDTO;
import com.inetum.realdolmen.hubkitbackend.models.Trailer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrailerMapper {
    Trailer fromDTO(TrailerDTO trailerDTO);

    TrailerDTO toDTO(Trailer trailer);

    @Mapping(target = "id", ignore = true)
    Trailer updateFromDTO(TrailerDTO insuranceAgencyDTO, @MappingTarget Trailer insuranceAgency);

    @Mapping(target = "id", ignore = true)
    Trailer updateFromEntity(Trailer newTrailer, @MappingTarget Trailer oldTrailer);
}