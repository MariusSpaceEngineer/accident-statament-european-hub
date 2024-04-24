package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.InsuranceAgency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceAgencyRepository extends JpaRepository<InsuranceAgency, Integer> {
    Optional<InsuranceAgency> findByNameAndAddressAndCountry(String name, String address, String country);

}
