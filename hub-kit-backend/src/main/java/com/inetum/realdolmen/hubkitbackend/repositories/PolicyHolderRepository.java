package com.inetum.realdolmen.hubkitbackend.repositories;


import com.inetum.realdolmen.hubkitbackend.models.InsuranceCertificate;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Integer> {
    @Query("SELECT p FROM PolicyHolder p JOIN p.insuranceCertificates c WHERE c.id = :certificateId")
    Optional<PolicyHolder> findByInsuranceCertificateId(@Param("certificateId") Integer certificateId);

}
