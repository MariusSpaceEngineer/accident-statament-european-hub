package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.InsuranceCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceCertificateRepository extends JpaRepository<InsuranceCertificate, Integer> {
    Optional<InsuranceCertificate> findByGreenCardNumberAndPolicyNumber(String greenCardNumber, String PolicyNumber);
}
