package com.inetum.realdolmen.hubkitbackend.repositories;

import com.inetum.realdolmen.hubkitbackend.models.Witness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WitnessRepository extends JpaRepository<Witness, Integer> {
    Optional<Witness> findByNameAndAddress(String name, String address);
}
