package com.inetum.realdolmen.hubkitbackend.repositories;


import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Integer> {

    Optional<PolicyHolder> findByEmail(String email);
}
