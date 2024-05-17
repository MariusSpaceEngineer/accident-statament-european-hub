package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.exceptions.AccidentStatementCreationFailed;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccidentStatementServiceTest {

    @InjectMocks
    private AccidentStatementService accidentStatementService;

    @Mock
    private AccidentStatementRepository accidentStatementRepository;
    @Mock
    private AccidentImageRepository accidentImageRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private WitnessRepository witnessRepository;
    @Mock
    private MotorRepository motorRepository;
    @Mock
    private TrailerRepository trailerRepository;
    @Mock
    private InsuranceCertificateRepository insuranceCertificateRepository;
    @Mock
    private InsuranceAgencyRepository insuranceAgencyRepository;
    @Mock
    private InsuranceCompanyRepository insuranceCompanyRepository;
    @Mock
    private PolicyHolderRepository policyHolderRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private AccidentStatementMapper accidentStatementMapper;
    @Mock
    private MotorMapper motorMapper;
    @Mock
    private TrailerMapper trailerMapper;
    @Mock
    private InsuranceCertificateMapper insuranceCertificateMapper;
    @Mock
    private InsuranceAgencyMapper insuranceAgencyMapper;
    @Mock
    private MailService mailService;

    @Test
    public void testCreateAccidentStatementThrowsException() throws Exception {
        // Arrange
        var driverA = DriverDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDate.parse("1980-01-01"))
                .address("123 Main St")
                .country("Belgium")
                .phoneNumber("1234567890")
                .email("john.doe@example.com")
                .drivingLicenseNr("123456")
                .category("B")
                .drivingLicenseExpirationDate(LocalDate.parse("2030-01-01"))
                .build();

        var driverB = DriverDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .birthday(LocalDate.parse("1990-02-02"))
                .address("456 Secondary St")
                .country("Belgium")
                .phoneNumber("0987654321")
                .email("jane.smith@example.com")
                .drivingLicenseNr("654321")
                .category("B")
                .drivingLicenseExpirationDate(LocalDate.parse("2035-02-02"))
                .build();

        var accidentImagesVehicleA = Collections.singletonList(AccidentImageDTO.builder().data(new byte[123]).build());
        var accidentImagesVehicleB = Collections.singletonList(AccidentImageDTO.builder().data(new byte[124]).build());

        var witness = WitnessDTO.builder()
                .name("Witness Name")
                .address("456 Witness St")
                .phoneNumber("0987654321")
                .build();

        var motorA = MotorDTO.builder()
                .markType("Brand")
                .licensePlate("1LOI958")
                .countryOfRegistration("Belgium")
                .build();

        var motorB = MotorDTO.builder()
                .markType("Brand")
                .licensePlate("2LOI958")
                .countryOfRegistration("Belgium")
                .build();

        var registeredTrailerA = TrailerDTO.builder()
                .hasRegistration(true)
                .licensePlate("DEF456")
                .countryOfRegistration("Belgium")
                .build();

        var unregisteredTrailerB = TrailerDTO.builder()
                .hasRegistration(false)
                .ofVehicle("Vehicle B")
                .build();

        InsuranceAgencyDTO insuranceAgencyPolicyHolderA = InsuranceAgencyDTO.builder()
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        InsuranceCompanyDTO insuranceCompanyPolicyHolderA = InsuranceCompanyDTO.builder()
                .name("Insurance Company")
                .build();

        InsuranceCertificateDTO insuranceCertificatePolicyHolderAMotor = InsuranceCertificateDTO.builder()
                .policyNumber("PO54894948")
                .greenCardNumber("GC498491989")
                .availabilityDate(LocalDate.parse("2024-01-01"))
                .expirationDate(LocalDate.parse("2025-01-01"))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgencyPolicyHolderA)
                .insuranceCompany(insuranceCompanyPolicyHolderA)
                .vehicle(motorA)
                .build();

        InsuranceCertificateDTO insuranceCertificatePolicyHolderATrailer = InsuranceCertificateDTO.builder()
                .policyNumber("PO2294988")
                .greenCardNumber("GC594984919")
                .availabilityDate(LocalDate.parse("2024-01-01"))
                .expirationDate(LocalDate.parse("2025-01-01"))
                .insuranceAgency(insuranceAgencyPolicyHolderA)
                .materialDamageCovered(false)
                .insuranceCompany(insuranceCompanyPolicyHolderA)
                .vehicle(registeredTrailerA)
                .build();

        var policyHolderVehicleA = PolicyHolderDTO.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificatePolicyHolderAMotor, insuranceCertificatePolicyHolderATrailer)))
                .build();

        InsuranceAgencyDTO insuranceAgencyPolicyHolderB = InsuranceAgencyDTO.builder()
                .name("Insurance Agency B")
                .address("890 Insurance Ave")
                .country("Belgium")
                .phoneNumber("2233445566")
                .email("agencyB@example.com")
                .build();

        InsuranceCompanyDTO insuranceCompanyPolicyHolderB = InsuranceCompanyDTO.builder()
                .name("Insurance Company B")
                .build();

        InsuranceCertificateDTO insuranceCertificatePolicyHolderBMotor = InsuranceCertificateDTO.builder()
                .policyNumber("PO198489552")
                .greenCardNumber("GC9874469")
                .availabilityDate(LocalDate.parse("2025-02-02"))
                .expirationDate(LocalDate.parse("2026-02-02"))
                .materialDamageCovered(true)
                .insuranceAgency(insuranceAgencyPolicyHolderB)
                .insuranceCompany(insuranceCompanyPolicyHolderB)
                .vehicle(motorB)
                .build();

        var policyHolderVehicleB = PolicyHolderDTO.builder()
                .firstName("Policy B")
                .lastName("Holder B")
                .email("policy.holderB@example.com")
                .address("456 Policy St")
                .postalCode("67890")
                .phoneNumber("0987654321")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificatePolicyHolderBMotor)))
                .build();

        AccidentStatementDTO accidentStatementDTO = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11").atStartOfDay())
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .witness(witness)
                .unregisteredTrailers(List.of(unregisteredTrailerB))
                .policyHolders(List.of(policyHolderVehicleA, policyHolderVehicleB))
                .vehicleACircumstances(List.of("PARKED/STOPPED"))
                .vehicleAInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleAVisibleDamageDescription("Visible Damage A")
                .vehicleAAccidentImages(accidentImagesVehicleA)
                .vehicleARemark("Remark A")
                .vehicleASignature(new byte[]{(byte) 1})
                .vehicleBCircumstances(List.of("PARKED/STOPPED"))
                .vehicleBInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleBVisibleDamageDescription("Visible Damage B")
                .vehicleBAccidentImages(accidentImagesVehicleB)
                .vehicleBRemark("Remark A")
                .vehicleBSignature(new byte[]{(byte) 1})
                .build();

        when(accidentStatementMapper.fromDTO(accidentStatementDTO)).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(AccidentStatementCreationFailed.class, () -> accidentStatementService.createAccidentStatement(accidentStatementDTO));
    }
}
