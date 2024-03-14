package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.exceptions.AccidentStatementCreationFailed;
import com.inetum.realdolmen.hubkitbackend.mappers.AccidentStatementMapper;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccidentStatementService {
    private final AccidentStatementRepository accidentStatementRepository;
    private final DriverRepository driverRepository;
    private final WitnessRepository witnessRepository;
    private final MotorRepository motorRepository;
    private final TrailerRepository trailerRepository;
    private final InsuranceCertificateRepository insuranceCertificateRepository;
    private final InsuranceAgencyRepository insuranceAgencyRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;

    private final AccidentStatementMapper accidentStatementMapper;

    public String createAccidentStatement(AccidentStatementDTO accidentStatementDTO) throws Exception {
        try {
            AccidentStatement accidentStatement = accidentStatementMapper.fromDTO(accidentStatementDTO);

            for (Driver driver : accidentStatement.getDrivers()) {
                var existingDriver = driverRepository.findByDrivingLicenseNr(driver.getDrivingLicenseNr());
                if (existingDriver.isPresent()) {
                    var index = accidentStatement.getDrivers().indexOf(driver);
                    accidentStatement.getDrivers().set(index, existingDriver.get());
                } else {
                    driverRepository.save(driver);
                }
            }

            for (Witness witness : accidentStatement.getWitnesses()) {
                var existingWitness = witnessRepository.findByNameAndAddress(witness.getName(), witness.getAddress());
                if (existingWitness.isPresent()) {
                    accidentStatement.getWitnesses().remove(witness);
                    accidentStatement.getWitnesses().add(existingWitness.get());
                } else {
                    witnessRepository.save(witness);
                }
            }

            if (accidentStatement.getMotors() != null) {

                for (Motor motor : accidentStatement.getMotors()) {
                    var existingMotor = motorRepository.findByLicensePlate(motor.getLicensePlate());
                    if (existingMotor.isPresent()) {
                        var index = accidentStatement.getMotors().indexOf(motor);
                        accidentStatement.getMotors().set(index, existingMotor.get());
                    } else {
                        motorRepository.save(motor);
                    }
                }
            }

            if (accidentStatement.getTrailers() != null) {

                for (Trailer trailer : accidentStatement.getTrailers()) {
                    var existingTrailer = trailerRepository.findByLicensePlate(trailer.getLicensePlate());
                    if (existingTrailer.isPresent()) {
                        var index = accidentStatement.getTrailers().indexOf(trailer);
                        accidentStatement.getTrailers().set(index, existingTrailer.get());
                    } else {
                        trailerRepository.save(trailer);
                    }
                }
            }

            for (InsuranceCertificate insuranceCertificate : accidentStatement.getInsuranceCertificates()) {
                var existingCertificate = insuranceCertificateRepository.findByGreenCardNumberAndPolicyNumber(insuranceCertificate.getGreenCardNumber(), insuranceCertificate.getPolicyNumber());

                if (existingCertificate.isPresent()) {
                    var index = accidentStatement.getInsuranceCertificates().indexOf(insuranceCertificate);
                    accidentStatement.getInsuranceCertificates().set(index, existingCertificate.get());
                } else {

                    var existingInsuranceAgency = insuranceAgencyRepository.findByNameAndAddress(
                            insuranceCertificate.getInsuranceAgency().getName(),
                            insuranceCertificate.getInsuranceAgency().getAddress());

                    var existingInsuranceCompany = insuranceCompanyRepository.findByName(
                            insuranceCertificate.getInsuranceCompany().getName());

                    if (existingInsuranceAgency.isPresent()) {
                        insuranceCertificate.setInsuranceAgency(existingInsuranceAgency.get());
                    } else {
                        insuranceAgencyRepository.save(insuranceCertificate.getInsuranceAgency());
                    }

                    if (existingInsuranceCompany.isPresent()) {
                        insuranceCertificate.setInsuranceCompany(existingInsuranceCompany.get());
                    } else {
                        insuranceCompanyRepository.save(insuranceCertificate.getInsuranceCompany());
                    }

                    insuranceCertificateRepository.save(insuranceCertificate);
                }
            }
            accidentStatementRepository.save(accidentStatement);

            return "Accident Statement created";
        } catch (DataAccessException e) {

            log.error("Error creating Accident Statement:", e);
            throw new AccidentStatementCreationFailed("Error occurred while creating Accident Statement");
        }
    }

    private <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }


}
