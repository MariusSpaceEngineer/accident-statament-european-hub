package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.exceptions.AccidentStatementCreationFailed;
import com.inetum.realdolmen.hubkitbackend.exceptions.FetchLocationAddressFailedException;
import com.inetum.realdolmen.hubkitbackend.exceptions.MissingPropertyException;
import com.inetum.realdolmen.hubkitbackend.mappers.AccidentStatementMapper;
import com.inetum.realdolmen.hubkitbackend.mappers.MotorMapper;
import com.inetum.realdolmen.hubkitbackend.mappers.TrailerMapper;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageReverseRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final MotorMapper motorMapper;
    private final TrailerMapper trailerMapper;

    @Value("${open-cage-api.key}")
    private String openCageApiKey;
    private JOpenCageGeocoder jOpenCageGeocoder;

    @PostConstruct
    public void init() {
        jOpenCageGeocoder = new JOpenCageGeocoder(openCageApiKey);
    }


    public String createAccidentStatement(AccidentStatementDTO accidentStatementDTO) throws Exception {
        try {

            AccidentStatement accidentStatement = accidentStatementMapper.fromDTO(accidentStatementDTO);
            assignAndMapVehicleDTOsToAccidentStatement(accidentStatementDTO, accidentStatement);

            saveAccidentImages(accidentStatement);

            saveDrivers(accidentStatement);

            saveWitness(accidentStatement);

            //Save motors
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

            //Save trailers
            if (accidentStatement.getTrailers() != null) {
                for (Trailer trailer : accidentStatement.getTrailers()) {
                    if (trailer.getHasRegistration()) {
                        var existingTrailer = trailerRepository.findByLicensePlate(trailer.getLicensePlate());
                        if (existingTrailer.isPresent()) {
                            var index = accidentStatement.getTrailers().indexOf(trailer);
                            accidentStatement.getTrailers().set(index, existingTrailer.get());
                        } else {
                            trailerRepository.save(trailer);
                        }
                    } else {
                        trailerRepository.save(trailer);
                    }
                }
            }

            //TODO check which fields can be null in policyHolder, InsuranceCertificate, InsuranceAgency and InsuranceCompany
            //TODO make sure the loop goes over all insurance certificates of a policy holder + assign the vehicles to it and check if they exist


            //Each policyHolder has insuranceCertificates, if those insuranceCertificates exist they have to be updated
            //If they don't exist they have to be created
            //If the policyholder exists and the insuranceCertificate is assigned to him also, they both have to be updated
            //If the policyholder exists and the insuranceCertificate is not assigned to him, it has to be assigned
            //If the policyholder doesn't exist it has to be made and the insuranceCertificate assigned to him

            // Save insurance certificates
            for (PolicyHolder policyHolder : accidentStatement.getPolicyHolders()) {
                var insuranceCertificate = policyHolder.getInsuranceCertificates().getFirst();
                if (insuranceCertificate.getGreenCardNumber() == null || insuranceCertificate.getGreenCardNumber().isEmpty() || insuranceCertificate.getPolicyNumber() == null || insuranceCertificate.getPolicyNumber().isEmpty()) {
                    throw new MissingPropertyException("PolicyHolder's green card number and policy number cannot be null");
                }
                var existingCertificate = insuranceCertificateRepository.findByGreenCardNumberAndPolicyNumber(insuranceCertificate.getGreenCardNumber(), insuranceCertificate.getPolicyNumber());
                if (existingCertificate.isEmpty()) {
                    var existingInsuranceAgency = insuranceAgencyRepository.findByNameAndAddressAndCountry(
                            insuranceCertificate.getInsuranceAgency().getName(),
                            insuranceCertificate.getInsuranceAgency().getAddress(),
                            insuranceCertificate.getInsuranceAgency().getCountry());

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

            policyHolderRepository.saveAll(accidentStatement.getPolicyHolders());

            accidentStatementRepository.save(accidentStatement);

            return "Accident Statement created";

        } catch (Exception e) {

            log.error("Error creating Accident Statement:", e);
            throw new AccidentStatementCreationFailed("Error occurred while creating Accident Statement");
        }
    }

    private void saveWitness(AccidentStatement accidentStatement) throws MissingPropertyException {
        var witness = accidentStatement.getWitness();
        if (witness != null) {
            if (witness.getName() == null || witness.getName().isEmpty() || witness.getAddress() == null || witness.getAddress().isEmpty()) {
                throw new MissingPropertyException("Witness's name and address cannot be null");
            }
            var existingWitness = witnessRepository.findByNameAndAddress(witness.getName(), witness.getAddress());
            if (existingWitness.isPresent()) {
                accidentStatement.setWitness(existingWitness.get());
            } else {
                witnessRepository.save(witness);
            }
        }
    }

    private void saveDrivers(AccidentStatement accidentStatement) {
        for (Driver driver : accidentStatement.getDrivers()) {
            var existingDriver = driverRepository.findByDrivingLicenseNr(driver.getDrivingLicenseNr());
            if (existingDriver.isPresent()) {
                var index = accidentStatement.getDrivers().indexOf(driver);
                accidentStatement.getDrivers().set(index, existingDriver.get());
            } else {
                driverRepository.save(driver);
            }
        }
    }

    private void saveAccidentImages(AccidentStatement accidentStatement) {
        if (accidentStatement.getVehicleAAccidentImages() != null) {
            accidentImageRepository.saveAll(accidentStatement.getVehicleAAccidentImages());
        }
        if (accidentStatement.getVehicleBAccidentImages() != null) {
            accidentImageRepository.saveAll(accidentStatement.getVehicleBAccidentImages());
        }
    }

    private void assignAndMapVehicleDTOsToAccidentStatement(AccidentStatementDTO accidentStatementDTO, AccidentStatement accidentStatement) {
        List<InsuranceCertificateDTO> policyHolderAInsuranceCertificates = accidentStatementDTO.getPolicyHolders().get(0).getInsuranceCertificates();
        List<InsuranceCertificateDTO> policyHolderBInsuranceCertificates = accidentStatementDTO.getPolicyHolders().get(1).getInsuranceCertificates();

        // Assign the vehicles of the policyholders accordingly
        for (int i = 0; i < accidentStatement.getPolicyHolders().size(); i++) {
            PolicyHolder policyHolder = accidentStatement.getPolicyHolders().get(i);
            List<InsuranceCertificateDTO> insuranceCertificatesDTO;
            if (i == 0) {
                insuranceCertificatesDTO = policyHolderAInsuranceCertificates;
            } else {
                insuranceCertificatesDTO = policyHolderBInsuranceCertificates;
            }

            // Iterate over the InsuranceCertificates and InsuranceCertificateDTOs in parallel
            for (int j = 0; j < policyHolder.getInsuranceCertificates().size(); j++) {
                InsuranceCertificate insuranceCertificate = policyHolder.getInsuranceCertificates().get(j);
                InsuranceCertificateDTO insuranceCertificateDTO = insuranceCertificatesDTO.get(j);

                // Call the mapper to convert VehicleDTO to Vehicle
                if (insuranceCertificateDTO.getVehicle() instanceof MotorDTO) {
                    Motor motor = motorMapper.fromDTO((MotorDTO) insuranceCertificateDTO.getVehicle());
                    insuranceCertificate.setVehicle(motor);
                } else if (insuranceCertificateDTO.getVehicle() instanceof TrailerDTO) {
                    Trailer trailer = trailerMapper.fromDTO((TrailerDTO) insuranceCertificateDTO.getVehicle());
                    insuranceCertificate.setVehicle(trailer);
                }
            }
        }
    }

    public String getLocationAddress(LocationCoordinates locationCoordinates) throws Exception {
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
