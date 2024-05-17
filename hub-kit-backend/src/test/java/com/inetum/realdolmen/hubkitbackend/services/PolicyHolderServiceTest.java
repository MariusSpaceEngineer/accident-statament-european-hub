package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyHolderServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private InsuranceCertificateRepository insuranceCertificateRepository;
    @Mock
    private InsuranceCompanyRepository insuranceCompanyRepository;
    @Mock
    private InsuranceAgencyRepository insuranceAgencyRepository;
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PolicyHolderMapper policyHolderMapper;
    @Mock
    private MotorMapper motorMapper;
    @Mock
    private InsuranceCertificateMapper insuranceCertificateMapper;
    @Mock
    private InsuranceCompanyMapper insuranceCompanyMapper;
    @Mock
    private InsuranceAgencyMapper insuranceAgencyMapper;
    @Mock
    private PolicyHolderPersonalInformationMapper personalInformationMapper;

    @InjectMocks
    private PolicyHolderService policyHolderService;

    private PolicyHolderDTO policyHolderDTO;
    private PolicyHolder policyHolder;

    @Test
    public void testFetchPolicyHolderProfile() {
        // Arrange
        String token = "testToken";
        var insuranceAgency = InsuranceAgency.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        var insuranceCompany = InsuranceCompany.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        var motor = Motor.builder()
                .id(1)
                .licensePlate("K4489ML")
                .countryOfRegistration("Belgium")
                .markType("Volkswagen")
                .build();

        var insuranceCertificate = InsuranceCertificate.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.of(2024, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgency)
                .insuranceCompany(insuranceCompany)
                .vehicle(motor)
                .build();

        policyHolder = PolicyHolder.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificate)))
                .build();

        var insuranceAgencyDTO = InsuranceAgencyDTO.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        var insuranceCompanyDTO = InsuranceCompanyDTO.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        var motorDTO = MotorDTO.builder()
                .id(1)
                .licensePlate("K4489ML")
                .countryOfRegistration("Belgium")
                .markType("Volkswagen")
                .build();

        var insuranceCertificateDTO = InsuranceCertificateDTO.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.of(2024, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgencyDTO)
                .insuranceCompany(insuranceCompanyDTO)
                .vehicle(motorDTO)
                .build();

        policyHolderDTO = PolicyHolderDTO.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificateDTO)))
                .build();

        when(policyHolderService.getUser(anyString())).thenReturn(Optional.of(policyHolder));
        when(policyHolderMapper.toDTO(policyHolder)).thenReturn(policyHolderDTO);

        // Act
        Optional<PolicyHolderDTO> result = policyHolderService.fetchPolicyHolderProfile(token);

        // Assert
        assertEquals(Optional.of(policyHolderDTO), result);
    }

    @Test
    public void testFetchPolicyHolderProfileUserNotFound() {
        // Arrange
        String token = "testToken";
        when(policyHolderService.getUser(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<PolicyHolderDTO> result = policyHolderService.fetchPolicyHolderProfile(token);

        // Assert
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testUpdatePolicyHolderPersonalInformationUserFound() throws Exception {
        // Arrange
        String token = "testToken";
        var insuranceAgency = InsuranceAgency.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        var insuranceCompany = InsuranceCompany.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        var motor = Motor.builder()
                .id(1)
                .licensePlate("K4489ML")
                .countryOfRegistration("Belgium")
                .markType("Volkswagen")
                .build();

        var insuranceCertificate = InsuranceCertificate.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.of(2024, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgency)
                .insuranceCompany(insuranceCompany)
                .vehicle(motor)
                .build();

        policyHolder = PolicyHolder.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificate)))
                .build();

        PolicyHolderPersonalInformationDTO policyHolderDTO = PolicyHolderPersonalInformationDTO.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("1298445")
                .phoneNumber("123454867890")
                .build();

        when(policyHolderService.getUser(anyString())).thenReturn(Optional.of(policyHolder));
        when(personalInformationMapper.toDTO(policyHolder)).thenReturn(policyHolderDTO);

        // Act
        Optional<PolicyHolderPersonalInformationDTO> result = policyHolderService.updatePolicyHolderPersonalInformation(token, policyHolderDTO);

        // Assert
        assertEquals(Optional.of(policyHolderDTO), result);
    }

    @Test
    public void testUpdatePolicyHolderPersonalInformationUserNotFound() throws Exception {
        // Arrange
        String token = "testToken";
        PolicyHolderPersonalInformationDTO policyHolderDTO = PolicyHolderPersonalInformationDTO.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .build();

        when(policyHolderService.getUser(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<PolicyHolderPersonalInformationDTO> result = policyHolderService.updatePolicyHolderPersonalInformation(token, policyHolderDTO);

        // Assert
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testUpdateInsuranceCertificateInformation_UserNotFound() throws Exception {
        String token = "testToken";
        var insuranceAgencyDTO = InsuranceAgencyDTO.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        var insuranceCompanyDTO = InsuranceCompanyDTO.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        var motorDTO = MotorDTO.builder()
                .id(1)
                .licensePlate("K4489ML")
                .countryOfRegistration("Belgium")
                .markType("Volkswagen")
                .build();

        var insuranceCertificateDTO = InsuranceCertificateDTO.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.of(2024, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgencyDTO)
                .insuranceCompany(insuranceCompanyDTO)
                .vehicle(motorDTO)
                .build();

        when(policyHolderService.getUser(token)).thenReturn(Optional.empty());

        Optional<List<InsuranceCertificateDTO>> result = policyHolderService.updateInsuranceCertificateInformation(token, insuranceCertificateDTO);

        assertTrue(result.isEmpty());
    }


    @Test(expected = Exception.class)
    public void testUpdateInsuranceCertificateInformation_GeneralException() throws Exception {
        String token = "testToken";
        var insuranceAgencyDTO = InsuranceAgencyDTO.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        var insuranceCompanyDTO = InsuranceCompanyDTO.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        var motorDTO = MotorDTO.builder()
                .id(1)
                .licensePlate("K4489ML")
                .countryOfRegistration("Belgium")
                .markType("Volkswagen")
                .build();

        var insuranceCertificateDTO = InsuranceCertificateDTO.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.of(2024, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgencyDTO)
                .insuranceCompany(insuranceCompanyDTO)
                .vehicle(motorDTO)
                .build();

        when(policyHolderService.getUser(token)).thenReturn(Optional.of(new User()));
        when(policyHolderService.getOrCreateVehicle(insuranceCertificateDTO)).thenThrow(new Exception("Internal server error"));

        policyHolderService.updateInsuranceCertificateInformation(token, insuranceCertificateDTO);
    }

}