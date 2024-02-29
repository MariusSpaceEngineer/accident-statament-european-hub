package com.inetum.realdolmen.hubkitbackend.utils;

import com.inetum.realdolmen.hubkitbackend.Roles;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceAgencyRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceCertificateRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.InsuranceCompanyRepository;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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


    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedInsuranceCompanyTable();
        seedInsuranceAgencyTable();
        seedInsuranceCertificateTable();
        seedPolicyHolderTable();
    }

    private void seedInsuranceCompanyTable() {
        String sql = "SELECT * FROM insurance_companies IC";
        List<InsuranceCompany> ic = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if (ic.isEmpty()) {
            var insuranceCompany = InsuranceCompany.builder()
                    .name("Ethias")
                    .build();

            insuranceCompanyRepository.save(insuranceCompany);

            log.info("Insurance Company Seeded");
        } else {
            log.info("Insurance Company Seeding Not Required");
        }

    }

    private void seedInsuranceAgencyTable() {
        String sql = "SELECT * FROM insurance_agencies IA";
        List<InsuranceAgency> ia = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if (ia.isEmpty()) {
            var insuranceAgency = InsuranceAgency.builder()
                    .name("Arces")
                    .address("Desguinlei 92, 2018 Antwerpen")
                    .country("BE")
                    .phoneNumber("032591970")
                    .email("Info@arces.be")
                    .build();

            insuranceAgencyRepository.save(insuranceAgency);

            log.info("Insurance Agency Seeded");
        } else {
            log.info("Insurance Agency Seeding Not Required");
        }

    }

    private void seedInsuranceCertificateTable() {
        String sql = "SELECT * FROM insurance_certificates IC";
        List<InsuranceCertificate> ic = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);
        if (ic.isEmpty()) {
            var insuranceCertificate = InsuranceCertificate.builder()
                    .policyNumber("POL123456789")
                    .greenCardNumber("GCN987654321")
                    .insuranceCertificateAvailabilityDate(LocalDate.now())
                    .insuranceCertificateExpirationDate(LocalDate.of(2025, 2, 28))
                    .build();

            var insuranceAgency = insuranceAgencyRepository.findByName("Arces");
            insuranceAgency.ifPresent(insuranceCertificate::setInsuranceAgency);

            var insuranceCompany = insuranceCompanyRepository.findByName("Ethias");
            insuranceCompany.ifPresent(insuranceCertificate::setInsuranceCompany);

            insuranceCertificateRepository.save(insuranceCertificate);

            log.info("Insurance Certificate Seeded");
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
                    .email("johndoe@gmail.com")
                    .role(Roles.POLICY_HOLDER)
                    .password(new BCryptPasswordEncoder().encode("1234"))
                    .build();

            var insuranceCertificate = insuranceCertificateRepository.findByGreenCardNumberAndPolicyNumber("GCN987654321", "POL123456789");
            insuranceCertificate.ifPresent(policyHolder::setInsuranceCertificate);

            userRepository.save(policyHolder);
            log.info("Policy Holder Seeded");
        } else {
            log.info("Policy Holder Seeding Not Required");
        }
    }


}
