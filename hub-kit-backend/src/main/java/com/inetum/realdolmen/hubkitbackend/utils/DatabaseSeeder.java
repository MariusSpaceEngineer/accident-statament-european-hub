package com.inetum.realdolmen.hubkitbackend.utils;

import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final InsuranceAgencyRepository insuranceAgencyRepository;
    private final InsuranceCertificateRepository insuranceCertificateRepository;
    private final MotorRepository motorRepository;
    private final TrailerRepository trailerRepository;


    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedInsuranceCertificateTable();
        seedPolicyHolderTable();
    }

    private void seedInsuranceCertificateTable() {
        String sql = "SELECT * FROM insurance_certificates IC";
        List<InsuranceCertificate> ic = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if (ic.isEmpty()) {
            var insuranceCompany = InsuranceCompany.builder()
                    .name("Ethias")
                    .build();
            insuranceCompanyRepository.save(insuranceCompany);

            var insuranceCompany2 = InsuranceCompany.builder()
                    .name("Argenta")
                    .build();
            insuranceCompanyRepository.save(insuranceCompany2);

            var insuranceAgency = InsuranceAgency.builder()
                    .name("Arces")
                    .address("Desguinlei 92, 2018 Antwerpen")
                    .country("BE")
                    .phoneNumber("032591970")
                    .email("Info@arces.be")
                    .build();
            insuranceAgencyRepository.save(insuranceAgency);

            var insuranceAgency2 = InsuranceAgency.builder()
                    .name("ING")
                    .address("Ringlaan 92, 2027 Antwerpen")
                    .country("BE")
                    .phoneNumber("032598950")
                    .email("Info@ing.be")
                    .build();
            insuranceAgencyRepository.save(insuranceAgency2);

            var motor = Motor.builder()
                    .markType("Volkswagen")
                    .licensePlate("6JIO87")
                    .countryOfRegistration("Belgium")
                    .build();
            motorRepository.save(motor);

            var trailer = Trailer.builder()
                    .hasRegistration(true)
                    .licensePlate("5HU489")
                    .countryOfRegistration("Belgium")
                    .build();
            trailerRepository.save(trailer);

            var insuranceCertificate1 = InsuranceCertificate.builder()
                    .policyNumber("POL123456789")
                    .greenCardNumber("GCN987654321")
                    .availabilityDate(LocalDate.now())
                    .expirationDate(LocalDate.of(2025, 2, 28))
                    .insuranceCompany(insuranceCompany)
                    .insuranceAgency(insuranceAgency)
                    .materialDamageCovered(false)
                    .vehicle(motor) // associate the motor with this certificate
                    .build();

            var insuranceCertificate2 = InsuranceCertificate.builder()
                    .policyNumber("POL987654321")
                    .greenCardNumber("GCN123456789")
                    .availabilityDate(LocalDate.now())
                    .expirationDate(LocalDate.of(2025, 2, 28))
                    .insuranceCompany(insuranceCompany2)
                    .insuranceAgency(insuranceAgency2)
                    .materialDamageCovered(true)
                    .vehicle(trailer) // associate the trailer with this certificate
                    .build();

            insuranceCertificateRepository.save(insuranceCertificate1);
            insuranceCertificateRepository.save(insuranceCertificate2);

            log.info("Insurance Certificates Seeded");
        } else {
            log.info("Insurance Certificate Seeding Not Required");
        }
    }

    private void seedPolicyHolderTable() {
        String sql = "SELECT * FROM policy_holders PH";
        List<PolicyHolder> ph = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if (ph.isEmpty()) {
            var policyHolder = PolicyHolder.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .address("Koningin Astridplein 27, 2018 Antwerpen")
                    .postalCode("2018")
                    .phoneNumber("0465879425")
                    .email("stoicamarius9010@gmail.com")
                    .role(Roles.POLICY_HOLDER)
                    .password(new BCryptPasswordEncoder().encode("Example_123"))
                    .build();

            var policyHolder2 = PolicyHolder.builder()
                    .firstName("John")
                    .lastName("Bravo")
                    .address("Bredabaan 256, 2027 Antwerpen")
                    .postalCode("2027")
                    .phoneNumber("0465879425")
                    .email("johnbravo@gmail.com")
                    .role(Roles.POLICY_HOLDER)
                    .password(new BCryptPasswordEncoder().encode("Example_123"))
                    .build();

            var insuranceCertificates = new ArrayList<InsuranceCertificate>(insuranceCertificateRepository.findAll());
            policyHolder.setInsuranceCertificates(insuranceCertificates);

            userRepository.save(policyHolder);
            userRepository.save(policyHolder2);
            log.info("Policy Holder Seeded");
        } else {
            log.info("Policy Holder Seeding Not Required");
        }
    }

}
