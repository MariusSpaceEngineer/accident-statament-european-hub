package com.inetum.realdolmen.hubkitbackend.interfaces.services;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.dto.LocationCoordinates;
import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;

import java.io.File;

public interface IAccidentStatementService {
    String createAccidentStatement(AccidentStatementDTO accidentStatementDTO) throws Exception;
    String getLocationAddress(LocationCoordinates locationCoordinates) throws Exception;
    File createPdf(AccidentStatement accidentStatement);
}
