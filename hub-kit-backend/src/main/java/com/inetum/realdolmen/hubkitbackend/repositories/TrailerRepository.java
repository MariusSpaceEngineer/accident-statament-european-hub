package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrailerRepository extends JpaRepository<Trailer, Integer> {
    Optional<Trailer> findByLicensePlate(String licensePlate);

}
