package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.dto.LocationCoordinates;
import com.inetum.realdolmen.hubkitbackend.exceptions.AccidentStatementCreationFailed;
import com.inetum.realdolmen.hubkitbackend.exceptions.FetchLocationAddressFailedException;
import com.inetum.realdolmen.hubkitbackend.exceptions.MissingPropertyException;
import com.inetum.realdolmen.hubkitbackend.mappers.AccidentStatementMapper;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageReverseRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccidentStatementService {
    private final AccidentStatementRepository accidentStatementRepository;
    private final AccidentImageRepository accidentImageRepository;
    private final DriverRepository driverRepository;
    private final WitnessRepository witnessRepository;
    private final MotorRepository motorRepository;
    private final TrailerRepository trailerRepository;
    private final InsuranceCertificateRepository insuranceCertificateRepository;
    private final InsuranceAgencyRepository insuranceAgencyRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final PolicyHolderRepository policyHolderRepository;
    private final AccidentStatementMapper accidentStatementMapper;
    @Value("${open-cage-api.key}")
    private String openCageApiKey;
    private JOpenCageGeocoder jOpenCageGeocoder;

    @PostConstruct
    public void init() {
        jOpenCageGeocoder = new JOpenCageGeocoder(openCageApiKey);
    }


    public String createAccidentStatement(AccidentStatementDTO accidentStatementDTO) throws Exception {
        try {
            if (accidentStatementDTO == null) {
                throw new MissingPropertyException("AccidentStatementDTO cannot be null");
            }

            AccidentStatement accidentStatement = accidentStatementMapper.fromDTO(accidentStatementDTO);

            if (!accidentStatement.getVehicleAAccidentImages().isEmpty()) {
                accidentImageRepository.saveAll(accidentStatement.getVehicleAAccidentImages());
            }
            if (!accidentStatement.getVehicleBAccidentImages().isEmpty()) {
                accidentImageRepository.saveAll(accidentStatement.getVehicleBAccidentImages());
            }

            for (Driver driver : accidentStatement.getDrivers()) {
                if (driver.getDrivingLicenseNr() == null || driver.getDrivingLicenseNr().isEmpty()) {
                    throw new MissingPropertyException("Driver's driving license number cannot be null");
                }
                var existingDriver = driverRepository.findByDrivingLicenseNr(driver.getDrivingLicenseNr());
                if (existingDriver.isPresent()) {
                    var index = accidentStatement.getDrivers().indexOf(driver);
                    accidentStatement.getDrivers().set(index, existingDriver.get());
                } else {
                    driverRepository.save(driver);
                }
            }

            for (Witness witness : accidentStatement.getWitnesses()) {
                if (witness.getName() == null || witness.getName().isEmpty() || witness.getAddress() == null || witness.getAddress().isEmpty()) {
                    throw new MissingPropertyException("Witness's name and address cannot be null");
                }
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
                    if (motor.getLicensePlate() == null || motor.getLicensePlate().isEmpty()) {
                        throw new MissingPropertyException("Motor's license plate cannot be null");
                    }
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
                    if (trailer.getLicensePlate() == null || trailer.getLicensePlate().isEmpty()) {
                        throw new MissingPropertyException("Trailer's license plate cannot be null");
                    }
                    var existingTrailer = trailerRepository.findByLicensePlate(trailer.getLicensePlate());
                    if (existingTrailer.isPresent()) {
                        var index = accidentStatement.getTrailers().indexOf(trailer);
                        accidentStatement.getTrailers().set(index, existingTrailer.get());
                    } else {
                        trailerRepository.save(trailer);
                    }
                }
            }

            for (PolicyHolder policyHolder : accidentStatement.getPolicyHolders()) {
                var insuranceCertificate = policyHolder.getInsuranceCertificates().getFirst();
                if (insuranceCertificate.getGreenCardNumber() == null || insuranceCertificate.getGreenCardNumber().isEmpty() || insuranceCertificate.getPolicyNumber() == null || insuranceCertificate.getPolicyNumber().isEmpty()) {
                    throw new MissingPropertyException("PolicyHolder's green card number and policy number cannot be null");
                }
                var existingCertificate = insuranceCertificateRepository.findByGreenCardNumberAndPolicyNumber(insuranceCertificate.getGreenCardNumber(), insuranceCertificate.getPolicyNumber());
                if (existingCertificate.isEmpty()) {
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
                    policyHolderRepository.save(policyHolder);

                } else {
                    var existingPolicyHolder = policyHolderRepository.findByInsuranceCertificateId(existingCertificate.get().getId());
                    if (existingPolicyHolder.isPresent()) {
                        var index = accidentStatement.getPolicyHolders().indexOf(policyHolder);
                        accidentStatement.getPolicyHolders().set(index, existingPolicyHolder.get());
                    }
                }
            }
            accidentStatementRepository.save(accidentStatement);

            return "Accident Statement created";

        } catch (Exception e) {

            log.error("Error creating Accident Statement:", e);
            throw new AccidentStatementCreationFailed("Error occurred while creating Accident Statement");
        }
    }

    public String getLocationAddress(LocationCoordinates locationCoordinates) throws Exception{
        try {
            JOpenCageReverseRequest request = new JOpenCageReverseRequest(locationCoordinates.getLatitude(), locationCoordinates.getLongitude());
            request.setNoAnnotations(true); // exclude additional info such as calling code, timezone, and currency
            request.setMinConfidence(3); // restrict to results with a confidence rating of at least 3 (out of 10)

            var response = jOpenCageGeocoder.reverse(request);

            return response.getResults().getFirst().getFormatted();
        } catch (Exception e) {
            log.error("Error while reverse geocoding location", e);
            throw new FetchLocationAddressFailedException("An error occurred when fetching location, please try again.");
        }
    }
}
