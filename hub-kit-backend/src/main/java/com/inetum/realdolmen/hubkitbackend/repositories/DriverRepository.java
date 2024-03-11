package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Integer> {
    Optional<Driver> findByDrivingLicenseNr(String drivingLicenseNr);
}
