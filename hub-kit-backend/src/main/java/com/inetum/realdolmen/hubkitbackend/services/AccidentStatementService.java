package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.exceptions.AccidentStatementCreationFailed;
import com.inetum.realdolmen.hubkitbackend.exceptions.FetchLocationAddressFailedException;
import com.inetum.realdolmen.hubkitbackend.exceptions.MissingPropertyException;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageReverseRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final VehicleRepository vehicleRepository;

    private final AccidentStatementMapper accidentStatementMapper;
    private final MotorMapper motorMapper;
    private final TrailerMapper trailerMapper;
    private final InsuranceCertificateMapper insuranceCertificateMapper;
    private final InsuranceAgencyMapper insuranceAgencyMapper;

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

            for (PolicyHolder policyHolder : accidentStatement.getPolicyHolders()) {
                List<InsuranceCertificate> updatedCertificates = new ArrayList<>();
                for (InsuranceCertificate insuranceCertificate : policyHolder.getInsuranceCertificates()) {
                    if (insuranceCertificate.getGreenCardNumber() == null || insuranceCertificate.getGreenCardNumber().isEmpty() || insuranceCertificate.getPolicyNumber() == null || insuranceCertificate.getPolicyNumber().isEmpty()) {
                        throw new MissingPropertyException("PolicyHolder's green card number and policy number cannot be null");
                    }
                    //Check to see if tge certificate exists
                    var existingCertificate = insuranceCertificateRepository.findByGreenCardNumberAndPolicyNumber(insuranceCertificate.getGreenCardNumber(), insuranceCertificate.getPolicyNumber());
                    //If it exists
                    if (existingCertificate.isPresent()) {
                        //First all the new properties have to be added from the new certificate to the existing certificate
                        var updatedCertificate = insuranceCertificateMapper.updateFromEntity(insuranceCertificate, existingCertificate.get());
                        updatedCertificate.setVehicle(insuranceCertificate.getVehicle());

                        //Check all the entities in the certificate if they exist and are updated
                        //If the insuranceCompany doesn't exist it saves it in the database
                        // otherwise it gets the one from the database
                        var insuranceCompany = insuranceCompanyRepository.findByName(updatedCertificate.getInsuranceCompany().getName());
                        if (insuranceCompany.isPresent()) {
                            updatedCertificate.setInsuranceCompany(insuranceCompany.get());
                        } else {
                            // Save the new InsuranceCompany to the database
                            updatedCertificate.setInsuranceCompany(insuranceCompanyRepository.save(updatedCertificate.getInsuranceCompany()));
                        }

                        //If the vehicle doesn't exist it saves it in the database
                        // otherwise it gets the one from the database and updates it with the new values and saves it
                        var vehicle = vehicleRepository.findVehicleByLicensePlate(updatedCertificate.getVehicle().getLicensePlate());
                        if (vehicle.isPresent()) {
                            if (vehicle.get() instanceof Motor) {
                                var updatedMotor = motorMapper.updateFromEntity((Motor) insuranceCertificate.getVehicle(), (Motor) vehicle.get());
                                // No need to save the updated Motor here, it will be saved when the transaction commits
                                updatedCertificate.setVehicle(updatedMotor);
                            } else if (vehicle.get() instanceof Trailer) {
                                var updatedTrailer = trailerMapper.updateFromEntity((Trailer) insuranceCertificate.getVehicle(), (Trailer) vehicle.get());
                                // No need to save the updated Trailer here, it will be saved when the transaction commits
                                updatedCertificate.setVehicle(updatedTrailer);
                            }
                        } else {
                            // Save the new Vehicle to the database
                            updatedCertificate.setVehicle(vehicleRepository.save(insuranceCertificate.getVehicle()));
                        }


                        var insuranceAgency = insuranceAgencyRepository.findByNameAndAddressAndCountry(updatedCertificate.getInsuranceAgency().getName(), updatedCertificate.getInsuranceAgency().getAddress(), updatedCertificate.getInsuranceAgency().getCountry());
                        if (insuranceAgency.isPresent()) {
                            var updatedInsuranceAgency = insuranceAgencyMapper.updateFromEntity(updatedCertificate.getInsuranceAgency(), insuranceAgency.get());
                            // Save the updated InsuranceAgency back to the database
                            updatedCertificate.setInsuranceAgency(updatedInsuranceAgency);
                        } else {
                            // Save the new InsuranceAgency to the database
                            updatedCertificate.setInsuranceAgency(insuranceAgencyRepository.save(updatedCertificate.getInsuranceAgency()));
                        }

                        // Save the updated InsuranceCertificate back to the database
                        // Set the insurance certificate to the policyholder
                        insuranceCertificate = insuranceCertificateRepository.save(updatedCertificate);
                        updatedCertificates.add(insuranceCertificate);
                    }
                    //If the certificate doesn't exist
                    else {
                        //First all the entities inside the certificate have to be checked and saved

                        //If the insuranceCompany doesn't exist it saves it in the database
                        // otherwise it gets the one from the database
                        var insuranceCompany = insuranceCompanyRepository.findByName(insuranceCertificate.getInsuranceCompany().getName());
                        if (insuranceCompany.isPresent()) {
                            insuranceCertificate.setInsuranceCompany(insuranceCompany.get());
                        } else {
                            // Save the new InsuranceCompany to the database
                            insuranceCertificate.setInsuranceCompany(insuranceCompanyRepository.save(insuranceCertificate.getInsuranceCompany()));
                        }

                        //If the vehicle doesn't exist it saves it in the database
                        // otherwise it gets the one from the database and updates it with the new values and saves it
                        var vehicle = vehicleRepository.findVehicleByLicensePlate(insuranceCertificate.getVehicle().getLicensePlate());
                        if (vehicle.isPresent()) {
                            if (vehicle.get() instanceof Motor) {
                                var updatedMotor = motorMapper.updateFromEntity((Motor) insuranceCertificate.getVehicle(), (Motor) vehicle.get());
                                // No need to save the updated Motor here, it will be saved when the transaction commits
                                insuranceCertificate.setVehicle(updatedMotor);
                            } else if (vehicle.get() instanceof Trailer) {
                                var updatedTrailer = trailerMapper.updateFromEntity((Trailer) insuranceCertificate.getVehicle(), (Trailer) vehicle.get());
                                // No need to save the updated Trailer here, it will be saved when the transaction commits
                                insuranceCertificate.setVehicle(updatedTrailer);
                            }
                        } else {
                            // Save the new Vehicle to the database
                            insuranceCertificate.setVehicle(vehicleRepository.save(insuranceCertificate.getVehicle()));
                        }

                        //If the insuranceAgency doesn't exist it saves it in the database
                        // otherwise it gets the one from the database and updates it with the new values and saves it
                        var insuranceAgency = insuranceAgencyRepository.findByNameAndAddressAndCountry(insuranceCertificate.getInsuranceAgency().getName(), insuranceCertificate.getInsuranceAgency().getAddress(), insuranceCertificate.getInsuranceAgency().getCountry());
                        if (insuranceAgency.isPresent()) {
                            var updatedInsuranceAgency = insuranceAgencyMapper.updateFromEntity(insuranceCertificate.getInsuranceAgency(), insuranceAgency.get());
                            insuranceCertificate.setInsuranceAgency(updatedInsuranceAgency);
                        } else {
                            // Save the new InsuranceAgency to the database
                            insuranceCertificate.setInsuranceAgency(insuranceAgencyRepository.save(insuranceCertificate.getInsuranceAgency()));

                        }

                        insuranceCertificate = insuranceCertificateRepository.save(insuranceCertificate);
                        updatedCertificates.add(insuranceCertificate);
                    }
                }

                policyHolder.setInsuranceCertificates(updatedCertificates);
                // Save the PolicyHolder
                var existingPolicyHolder = policyHolderRepository.findByInsuranceCertificateId(policyHolder.getInsuranceCertificates().getFirst().getId());
                if (existingPolicyHolder.isEmpty()) {
                    // If the PolicyHolder doesn't exist, save it
                    var savedPolicyHolder = policyHolderRepository.save(policyHolder);
                    var index = accidentStatement.getPolicyHolders().indexOf(policyHolder);
                    // Set the saved PolicyHolder to the accidentStatement
                    accidentStatement.getPolicyHolders().set(index, savedPolicyHolder);
                } else {
                    // If the PolicyHolder already exists, set it to the accidentStatement
                    var index = accidentStatement.getPolicyHolders().indexOf(policyHolder);
                    accidentStatement.getPolicyHolders().set(index, existingPolicyHolder.get());
                }

            }

            createPdf(accidentStatement);

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

    private void assignAndMapVehicleDTOsToAccidentStatement(AccidentStatementDTO
                                                                    accidentStatementDTO, AccidentStatement accidentStatement) {
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

    public void createPdf(AccidentStatement accidentStatement) {
        try {
            // Get the user's desktop path
            String desktopPath = "C:\\Users\\MSICS85\\OneDrive - RealDolmen\\Desktop\\";

            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(desktopPath + "AccidentStatement.pdf");

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf, PageSize.A4);

            // Create a table with two columns
            Table table = new Table(2);

            // Add the general information to the first row, spanning both columns
            Cell generalCell = new Cell(1, 2);
            addGeneralInformationToDocument(accidentStatement, generalCell);
            table.addCell(generalCell);

            // Add the vehicle A and B information to the second row in separate columns
            Cell vehicleACell = new Cell(1, 1);
            addVehicleAInformationToDocument(accidentStatement, vehicleACell);
            table.addCell(vehicleACell);

            Cell vehicleBCell = new Cell(1, 1);
            addVehicleBInformationToDocument(accidentStatement, vehicleBCell);
            table.addCell(vehicleBCell);

            // Add the table to the document
            document.add(table);

            // Close document
            document.close();
        } catch (Exception e) {
            System.out.println("Error occurred while creating PDF: " + e.getMessage());
        }
    }


    private void addVehicleBInformationToDocument(AccidentStatement accidentStatement, Cell document) {
        Text policyHolderTitle = createBoldResizedText("Policy Holder B", 14);
        document.add(new Paragraph(policyHolderTitle));
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Name: " + accidentStatement.getPolicyHolders().getLast().getLastName()));
        document.add(new Paragraph("First Name: " + accidentStatement.getPolicyHolders().getLast().getFirstName()));
        document.add(new Paragraph("Address: " + accidentStatement.getPolicyHolders().getLast().getAddress()));
        document.add(new Paragraph("Postal Code: " + accidentStatement.getPolicyHolders().getLast().getPostalCode()));
        document.add(new Paragraph("Phone Number: " + accidentStatement.getPolicyHolders().getLast().getPhoneNumber()));
        document.add(new Paragraph("Email: " + accidentStatement.getPolicyHolders().getLast().getEmail()));
        document.add(new Paragraph("\n\n\n\n"));

        Text vehicleTitle = createBoldText("Vehicle");
        document.add(new Paragraph(vehicleTitle));

        PolicyHolder policyHolderVehicleB = accidentStatement.getPolicyHolders().getLast();
        List<InsuranceCertificate> insuranceCertificates = policyHolderVehicleB.getInsuranceCertificates();
        for (InsuranceCertificate insuranceCertificate : insuranceCertificates) {
            if (insuranceCertificate.getVehicle() instanceof Motor motor) {
                Text motorTitle = createBoldText("Motor");
                document.add(new Paragraph(motorTitle));

                document.add(new Paragraph("Mark, Type: " + motor.getMarkType()));
                document.add(new Paragraph("Registration Number: " + motor.getLicensePlate()));
                document.add(new Paragraph("Country of Registration: " + motor.getCountryOfRegistration()));
                document.add(new Paragraph("\n"));

                Text insuranceCompanyTitle = createBoldText("Insurance Company");
                document.add(new Paragraph(insuranceCompanyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceCompany().getName()));
                document.add(new Paragraph("Policy Number: " + insuranceCertificate.getPolicyNumber()));
                document.add(new Paragraph("Green Card Number: " + insuranceCertificate.getGreenCardNumber()));
                document.add(new Paragraph("Insurance Certificate Availability Date: " + insuranceCertificate.getAvailabilityDate()));
                document.add(new Paragraph("From: " + insuranceCertificate.getAvailabilityDate() + " To: " + insuranceCertificate.getExpirationDate()));
                document.add(new Paragraph("\n"));

                Text insuranceAgencyTitle = createBoldText("Insurance Agency");
                document.add(new Paragraph(insuranceAgencyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceAgency().getName()));
                document.add(new Paragraph("Address: " + insuranceCertificate.getInsuranceAgency().getAddress()));
                document.add(new Paragraph("Country: " + insuranceCertificate.getInsuranceAgency().getCountry()));
                document.add(new Paragraph("Phone Number: " + insuranceCertificate.getInsuranceAgency().getPhoneNumber()));
                document.add(new Paragraph("Email: " + insuranceCertificate.getInsuranceAgency().getEmail()));
                document.add(new Paragraph("\n"));
                //Material damage covered
                break;
            }
        }
        for (InsuranceCertificate insuranceCertificate : insuranceCertificates) {
            if (insuranceCertificate.getVehicle() instanceof Trailer trailer) {
                Text trailerTitle = createBoldText("Trailer");
                document.add(new Paragraph(trailerTitle));

                document.add(new Paragraph("Registration Number: " + trailer.getLicensePlate()));
                document.add(new Paragraph("Country of Registration: " + trailer.getCountryOfRegistration()));
                document.add(new Paragraph("\n"));

                Text insuranceCompanyTitle = createBoldText("Insurance Company");
                document.add(new Paragraph(insuranceCompanyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceCompany().getName()));
                document.add(new Paragraph("Policy Number: " + insuranceCertificate.getPolicyNumber()));
                document.add(new Paragraph("Green Card Number: " + insuranceCertificate.getGreenCardNumber()));
                document.add(new Paragraph("Insurance Certificate Availability Date: " + insuranceCertificate.getAvailabilityDate()));
                document.add(new Paragraph("From: " + insuranceCertificate.getAvailabilityDate() + " To: " + insuranceCertificate.getExpirationDate()));
                document.add(new Paragraph("\n"));

                Text insuranceAgencyTitle = createBoldText("Insurance Agency");
                document.add(new Paragraph(insuranceAgencyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceAgency().getName()));
                document.add(new Paragraph("Address: " + insuranceCertificate.getInsuranceAgency().getAddress()));
                document.add(new Paragraph("Country: " + insuranceCertificate.getInsuranceAgency().getCountry()));
                document.add(new Paragraph("Phone Number: " + insuranceCertificate.getInsuranceAgency().getPhoneNumber()));
                document.add(new Paragraph("Email: " + insuranceCertificate.getInsuranceAgency().getEmail()));
                document.add(new Paragraph("\n"));

                //Material damage covered
                break;
            }
        }
        Text driverTitle = createBoldText("Driver");
        document.add(new Paragraph(driverTitle));

        document.add(new Paragraph("Name: " + accidentStatement.getDrivers().getLast().getLastName()));
        document.add(new Paragraph("First Name: " + accidentStatement.getDrivers().getLast().getFirstName()));
        document.add(new Paragraph("Date of Birth: " + accidentStatement.getDrivers().getLast().getBirthday()));
        document.add(new Paragraph("Address: " + accidentStatement.getDrivers().getLast().getAddress()));
        document.add(new Paragraph("Country: " + accidentStatement.getDrivers().getLast().getCountry()));
        document.add(new Paragraph("Phone Number: " + accidentStatement.getDrivers().getLast().getPhoneNumber()));
        document.add(new Paragraph("E-mail: " + accidentStatement.getDrivers().getLast().getEmail()));
        document.add(new Paragraph("Driving License"));
        document.add(new Paragraph("License Number: " + accidentStatement.getDrivers().getLast().getDrivingLicenseNr()));
        document.add(new Paragraph("Category: " + accidentStatement.getDrivers().getLast().getCategory()));
        document.add(new Paragraph("Expiration Date: " + accidentStatement.getDrivers().getLast().getDrivingLicenseExpirationDate()));
        document.add(new Paragraph("\n"));

        byte[] vehicleBInitialImpactSketch = accidentStatement.getVehicleBInitialImpactSketch();
        if (vehicleBInitialImpactSketch != null) {
            ImageData imageData = ImageDataFactory.create(vehicleBInitialImpactSketch);
            Image image = new Image(imageData);
            image.scaleToFit(500, 500);  // Set the size of the image
            Text vehicleBPointOfImpact = createBoldText("Vehicle B Point of Impact:");
            document.add(new Paragraph(vehicleBPointOfImpact));
            document.add(image);
        }
        document.add(new Paragraph("Visible Damage Vehicle B: " + accidentStatement.getVehicleBVisibleDamageDescription()));
        document.add(new Paragraph("Remarks Vehicle B: " + accidentStatement.getVehicleBRemark()));
        byte[] driverBSignature = accidentStatement.getVehicleBSignature();
        if (driverBSignature != null) {
            ImageData imageData = ImageDataFactory.create(driverBSignature);
            Image image = new Image(imageData);
            image.scaleToFit(150, 150);  // Set the size of the image
            Text driverBSignatureTitle = createBoldText("Driver B Signature");
            document.add(new Paragraph(driverBSignatureTitle));
            document.add(image);
        }
    }

    private void addVehicleAInformationToDocument(AccidentStatement accidentStatement, Cell document) {
        Text policyHolderTitle = createBoldResizedText("Policy Holder A", 14);
        document.add(new Paragraph(policyHolderTitle));
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Name: " + accidentStatement.getPolicyHolders().getFirst().getLastName()));
        document.add(new Paragraph("First Name: " + accidentStatement.getPolicyHolders().getFirst().getFirstName()));
        document.add(new Paragraph("Address: " + accidentStatement.getPolicyHolders().getFirst().getAddress()));
        document.add(new Paragraph("Postal Code: " + accidentStatement.getPolicyHolders().getFirst().getPostalCode()));
        document.add(new Paragraph("Phone Number: " + accidentStatement.getPolicyHolders().getFirst().getPhoneNumber()));
        document.add(new Paragraph("Email: " + accidentStatement.getPolicyHolders().getFirst().getEmail()));
        document.add(new Paragraph("\n"));

        Text vehicleTitle = createBoldText("Vehicle");
        document.add(new Paragraph(vehicleTitle));

        PolicyHolder policyHolderVehicleA = accidentStatement.getPolicyHolders().getFirst();
        List<InsuranceCertificate> insuranceCertificates = policyHolderVehicleA.getInsuranceCertificates();
        for (InsuranceCertificate insuranceCertificate : insuranceCertificates) {
            if (insuranceCertificate.getVehicle() instanceof Motor motor) {
                Text motorTitle = createBoldText("Motor");
                document.add(new Paragraph(motorTitle));

                document.add(new Paragraph("Mark, Type: " + motor.getMarkType()));
                document.add(new Paragraph("Registration Number: " + motor.getLicensePlate()));
                document.add(new Paragraph("Country of Registration: " + motor.getCountryOfRegistration()));
                document.add(new Paragraph("\n"));

                Text insuranceCompanyTitle = createBoldText("Insurance Company");
                document.add(new Paragraph(insuranceCompanyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceCompany().getName()));
                document.add(new Paragraph("Policy Number: " + insuranceCertificate.getPolicyNumber()));
                document.add(new Paragraph("Green Card Number: " + insuranceCertificate.getGreenCardNumber()));
                document.add(new Paragraph("Insurance Certificate Availability Date: " + insuranceCertificate.getAvailabilityDate()));
                document.add(new Paragraph("From: " + insuranceCertificate.getAvailabilityDate() + " To: " + insuranceCertificate.getExpirationDate()));
                document.add(new Paragraph("\n"));

                Text insuranceAgencyTitle = createBoldText("Insurance Agency");
                document.add(new Paragraph(insuranceAgencyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceAgency().getName()));
                document.add(new Paragraph("Address: " + insuranceCertificate.getInsuranceAgency().getAddress()));
                document.add(new Paragraph("Country: " + insuranceCertificate.getInsuranceAgency().getCountry()));
                document.add(new Paragraph("Phone Number: " + insuranceCertificate.getInsuranceAgency().getPhoneNumber()));
                document.add(new Paragraph("Email: " + insuranceCertificate.getInsuranceAgency().getEmail()));
                document.add(new Paragraph("\n"));
                //Material damage covered
                break;
            }
        }
        for (InsuranceCertificate insuranceCertificate : insuranceCertificates) {
            if (insuranceCertificate.getVehicle() instanceof Trailer trailer) {
                Text trailerTitle = createBoldText("Trailer");
                document.add(new Paragraph(trailerTitle));

                document.add(new Paragraph("Registration Number: " + trailer.getLicensePlate()));
                document.add(new Paragraph("Country of Registration: " + trailer.getCountryOfRegistration()));
                document.add(new Paragraph("\n"));

                Text insuranceCompanyTitle = createBoldText("Insurance Company");
                document.add(new Paragraph(insuranceCompanyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceCompany().getName()));
                document.add(new Paragraph("Policy Number: " + insuranceCertificate.getPolicyNumber()));
                document.add(new Paragraph("Green Card Number: " + insuranceCertificate.getGreenCardNumber()));
                document.add(new Paragraph("Insurance Certificate Availability Date: " + insuranceCertificate.getAvailabilityDate()));
                document.add(new Paragraph("From: " + insuranceCertificate.getAvailabilityDate() + " To: " + insuranceCertificate.getExpirationDate()));
                document.add(new Paragraph("\n"));

                Text insuranceAgencyTitle = createBoldText("Insurance Agency");
                document.add(new Paragraph(insuranceAgencyTitle));

                document.add(new Paragraph("Name: " + insuranceCertificate.getInsuranceAgency().getName()));
                document.add(new Paragraph("Address: " + insuranceCertificate.getInsuranceAgency().getAddress()));
                document.add(new Paragraph("Country: " + insuranceCertificate.getInsuranceAgency().getCountry()));
                document.add(new Paragraph("Phone Number: " + insuranceCertificate.getInsuranceAgency().getPhoneNumber()));
                document.add(new Paragraph("Email: " + insuranceCertificate.getInsuranceAgency().getEmail()));
                document.add(new Paragraph("\n"));

                //Material damage covered
                break;
            }
        }
        Text driverTitle = createBoldText("Driver");
        document.add(new Paragraph(driverTitle));

        document.add(new Paragraph("Name: " + accidentStatement.getDrivers().getFirst().getLastName()));
        document.add(new Paragraph("First Name: " + accidentStatement.getDrivers().getFirst().getFirstName()));
        document.add(new Paragraph("Date of Birth: " + accidentStatement.getDrivers().getFirst().getBirthday()));
        document.add(new Paragraph("Address: " + accidentStatement.getDrivers().getFirst().getAddress()));
        document.add(new Paragraph("Country: " + accidentStatement.getDrivers().getFirst().getCountry()));
        document.add(new Paragraph("Phone Number: " + accidentStatement.getDrivers().getFirst().getPhoneNumber()));
        document.add(new Paragraph("E-mail: " + accidentStatement.getDrivers().getFirst().getEmail()));
        document.add(new Paragraph("Driving License"));
        document.add(new Paragraph("License Number: " + accidentStatement.getDrivers().getFirst().getDrivingLicenseNr()));
        document.add(new Paragraph("Category: " + accidentStatement.getDrivers().getFirst().getCategory()));
        document.add(new Paragraph("Expiration Date: " + accidentStatement.getDrivers().getFirst().getDrivingLicenseExpirationDate()));
        document.add(new Paragraph("\n"));

        byte[] vehicleAInitialImpactSketch = accidentStatement.getVehicleAInitialImpactSketch();
        if (vehicleAInitialImpactSketch != null) {
            ImageData imageData = ImageDataFactory.create(vehicleAInitialImpactSketch);
            Image image = new Image(imageData);
            image.scaleToFit(500, 500);  // Set the size of the image
            Text vehicleAPointOfImpact = createBoldText("Vehicle A Point of Impact:");
            document.add(new Paragraph(vehicleAPointOfImpact));
            document.add(image);
        }
        document.add(new Paragraph("Visible Damage Vehicle A: " + accidentStatement.getVehicleAVisibleDamageDescription()));
        document.add(new Paragraph("Remarks Vehicle A: " + accidentStatement.getVehicleARemark()));
        byte[] driverASignature = accidentStatement.getVehicleASignature();
        if (driverASignature != null) {
            ImageData imageData = ImageDataFactory.create(driverASignature);
            Image image = new Image(imageData);
            image.scaleToFit(150, 150);  // Set the size of the image
            Text driverASignatureTitle = createBoldText("Driver A Signature");
            document.add(new Paragraph(driverASignatureTitle));
            document.add(image);
        }
    }

    private void addGeneralInformationToDocument(AccidentStatement accidentStatement, Cell document) {
        Text accidentStatementTitle = createBoldResizedText("Accident Statement", 20);
        document.add(new Paragraph(accidentStatementTitle));
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Date of Accident: " + accidentStatement.getDate()));
        document.add(new Paragraph("Location: " + accidentStatement.getLocation()));
        document.add(new Paragraph("Injured: " + accidentStatement.getInjured()));
        document.add(new Paragraph("\n"));

        Text materialDamageTitle = createBoldResizedText("Material Damage", 14);
        document.add(new Paragraph(materialDamageTitle));

        document.add(new Paragraph("Damage to Other Vehicles: " + (accidentStatement.getDamageToOtherCars() ? "Yes" : "No")));
        document.add(new Paragraph("Damage to Other Objects: " + (accidentStatement.getDamageToObjects() ? "Yes" : "No")));
        document.add(new Paragraph("\n"));

        Text witnessTitle = createBoldResizedText("Witness", 14);
        document.add(new Paragraph(witnessTitle));

        document.add(new Paragraph("Name: " + accidentStatement.getWitness().getName()));
        document.add(new Paragraph("Address: " + accidentStatement.getWitness().getAddress()));
        document.add(new Paragraph("Phone Number: " + accidentStatement.getWitness().getPhoneNumber()));
        document.add(new Paragraph("\n"));

        byte[] accidentSketch = accidentStatement.getSketchOfAccident();
        if (accidentSketch != null) {
            ImageData imageData = ImageDataFactory.create(accidentSketch);
            Image image = new Image(imageData);
            image.scaleToFit(500, 500);  // Set the size of the image

            Text accidentSketchTitle = createBoldResizedText("Accident Sketch", 14);
            document.add(new Paragraph(accidentSketchTitle));

            document.add(image);
        }
    }

    public Text createBoldText(String text) {
        return new Text(text)
                .setBold();
    }

    public Text createBoldResizedText(String text, Integer fontSize) {

        return new Text(text)
                .setBold()
                .setFontSize(fontSize);
    }

}
