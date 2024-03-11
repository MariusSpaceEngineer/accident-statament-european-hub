package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.Motor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MotorRepository extends JpaRepository<Motor, Integer> {
    Optional<Motor> findByLicensePlate(String licensePlate);
}
