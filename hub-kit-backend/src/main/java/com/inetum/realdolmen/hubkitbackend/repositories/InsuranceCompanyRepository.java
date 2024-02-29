package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.InsuranceCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceCompanyRepository  extends JpaRepository<InsuranceCompany, Integer> {
    Optional<InsuranceCompany> findByName(String name);

}
