package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.exceptions.VehicleMismatchException;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyHolderService {
    private final UserRepository userRepository;
    private final InsuranceCertificateRepository insuranceCertificateRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final InsuranceAgencyRepository insuranceAgencyRepository;
    private final VehicleRepository vehicleRepository;
    private final JwtService jwtService;

    private final PolicyHolderMapper policyHolderMapper;
    private final InsuranceCertificateMapper insuranceCertificateMapper;
    private final InsuranceCompanyMapper insuranceCompanyMapper;
    private final InsuranceAgencyMapper insuranceAgencyMapper;
    private final PolicyHolderPersonalInformationMapper personalInformationMapper;
    private final MotorMapper motorMapper;
    private final TrailerMapper trailerMapper;

    public Optional<PolicyHolderDTO> fetchPolicyHolderProfile(String token) {
        Optional<User> user = getUser(token);

        if (user.isPresent()) {
            PolicyHolder policyHolder = (PolicyHolder) user.get();
            PolicyHolderDTO dto = policyHolderMapper.toDTO(policyHolder);
            addVehiclesDTOsToPolicyHolderDTO(policyHolder, dto);

            return Optional.of(dto);
        } else {
            // User not found
            return Optional.empty();
        }
    }

    public Optional<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(String token, PolicyHolderPersonalInformationDTO policyHolderDTO) throws Exception {
        Optional<User> user = getUser(token);

        if (user.isPresent()) {
            PolicyHolder policyHolder = (PolicyHolder) user.get();
            personalInformationMapper.updateFromDTO(policyHolderDTO, policyHolder);

            userRepository.save(policyHolder);
            return Optional.of(personalInformationMapper.toDTO(policyHolder));
        } else {
            // User not found
            return Optional.empty();
        }
    }


    public Optional<List<InsuranceCertificateDTO>> updateInsuranceCertificateInformation(String token, InsuranceCertificateDTO insuranceCertificateDTO) throws Exception {

        try {
            Optional<User> user = getUser(token);

            if (user.isPresent()) {
                PolicyHolder existingPolicyHolder = (PolicyHolder) user.get();

                InsuranceCompany savedInsuranceCompany = getOrCreateInsuranceCompany(insuranceCertificateDTO);
                InsuranceAgency savedInsuranceAgency = getOrCreateInsuranceAgency(insuranceCertificateDTO);
                Vehicle savedVehicle = getOrCreateVehicle(insuranceCertificateDTO);

                //If the policyHolder has no insurance certificate
                if (existingPolicyHolder.getInsuranceCertificates().isEmpty()) {
                    addInsuranceCertificateToPolicyHolder(insuranceCertificateDTO, existingPolicyHolder, savedInsuranceCompany, savedInsuranceAgency, savedVehicle);
                } else {
                    updateInsuranceCertificateOfPolicyHolder(insuranceCertificateDTO, existingPolicyHolder, savedInsuranceCompany, savedInsuranceAgency, savedVehicle);
                }

                insuranceCertificateRepository.saveAll(existingPolicyHolder.getInsuranceCertificates());
                userRepository.save(existingPolicyHolder);

                List<InsuranceCertificateDTO> result = new ArrayList<>();
                addVehicleDTOsToInsuranceCertificatesDTOs(existingPolicyHolder, result);

                return Optional.of(result);
            } else {
                // User not found
                return Optional.empty();
            }
        } catch (VehicleMismatchException e) {
            log.error("Unable to change the type of an existing vehicle:", e);
            throw new VehicleMismatchException("You can't change the vehicle type.");
        } catch (Exception e) {
            log.error("Unexpected error during updating user's insurance certificate:", e);
            throw new Exception("Internal server error");
        }
    }
    /**
     * This method adds VehicleDTOs to a list of InsuranceCertificateDTOs for a given PolicyHolder.
     * It iterates over the InsuranceCertificates of the PolicyHolder, converts each InsuranceCertificate to a DTO,
     * and sets the appropriate VehicleDTO (MotorDTO or TrailerDTO) based on the type of Vehicle associated with the InsuranceCertificate.
     * The resulting InsuranceCertificateDTO is then added to the provided result list.
     *
     * @param existingPolicyHolder The PolicyHolder whose InsuranceCertificates are to be processed.
     * @param result The list of InsuranceCertificateDTOs to which the new InsuranceCertificateDTOs will be added.
     */
    private void addVehicleDTOsToInsuranceCertificatesDTOs(PolicyHolder existingPolicyHolder, List<InsuranceCertificateDTO> result) {
        for (InsuranceCertificate insurance : existingPolicyHolder.getInsuranceCertificates()) {
            var insuranceDTO = insuranceCertificateMapper.toDTO(insurance);
            Vehicle vehicle = insurance.getVehicle();
            if (vehicle instanceof Motor) {
                MotorDTO motorDTO = motorMapper.toDTO((Motor) vehicle);
                insuranceDTO.setVehicle(motorDTO);
            } else if (vehicle instanceof Trailer) {
                TrailerDTO trailerDTO = trailerMapper.toDTO((Trailer) vehicle);
                insuranceDTO.setVehicle(trailerDTO);
            }

            result.add(insuranceDTO);
        }
    }

    Optional<User> getUser(String token) {
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email);
    }

    /**
     * This method is used to add vehicle data transfer objects (DTOs) to a policyholder DTO.
     * It iterates over the insurance certificates of the policyholder, and for each certificate,
     * it checks the type of the associated vehicle. If the vehicle is an instance of Motor,
     * it maps the Motor to a MotorDTO and sets it as the vehicle for the corresponding insurance certificate in the DTO.
     * Similarly, if the vehicle is an instance of Trailer, it maps the Trailer to a TrailerDTO and sets it as the vehicle.
     *
     * @param policyHolder The policyholder from which the insurance certificates are obtained.
     * @param dto          The policyholder DTO to which the vehicle DTOs are added.
     */
    private void addVehiclesDTOsToPolicyHolderDTO(PolicyHolder policyHolder, PolicyHolderDTO dto) {
        List<InsuranceCertificate> insuranceCertificates = policyHolder.getInsuranceCertificates();
        for (int i = 0; i < insuranceCertificates.size(); i++) {
            Vehicle vehicle = insuranceCertificates.get(i).getVehicle();
            if (vehicle instanceof Motor) {
                MotorDTO motorDTO = motorMapper.toDTO((Motor) vehicle);
                dto.getInsuranceCertificates().get(i).setVehicle(motorDTO);
            } else if (vehicle instanceof Trailer) {
                TrailerDTO trailerDTO = trailerMapper.toDTO((Trailer) vehicle);
                dto.getInsuranceCertificates().get(i).setVehicle(trailerDTO);
            }
        }
    }
    /**
     * This method retrieves an existing InsuranceCompany from the database or creates a new one if it doesn't exist.
     * It first checks if the InsuranceCompanyDTO has an ID. If it does, it tries to find the InsuranceCompany in the database by ID.
     * If the ID is not present or the InsuranceCompany is not found by ID, it then checks if the InsuranceCompanyDTO has a name. If it does, it tries to find the InsuranceCompany in the database by name.
     * If the InsuranceCompany is found, it updates the InsuranceCompany with the data from the InsuranceCompanyDTO and saves it to the database.
     * If the InsuranceCompany is not found, it creates a new InsuranceCompany from the InsuranceCompanyDTO and saves it to the database.
     *
     * @param insuranceCertificateDTO The InsuranceCertificateDTO that contains the InsuranceCompanyDTO.
     * @return The existing or newly created InsuranceCompany.
     */
    private InsuranceCompany getOrCreateInsuranceCompany(InsuranceCertificateDTO insuranceCertificateDTO) {
        InsuranceCompanyDTO insuranceCompanyDTO = insuranceCertificateDTO.getInsuranceCompany();
        Optional<InsuranceCompany> existingInsuranceCompany = Optional.empty();

        if (insuranceCompanyDTO.getId() != null) {
            existingInsuranceCompany = insuranceCompanyRepository.findById(insuranceCompanyDTO.getId());
        } else if (insuranceCompanyDTO.getName() != null) {
            existingInsuranceCompany = insuranceCompanyRepository.findByName(insuranceCompanyDTO.getName());
        }

        if (existingInsuranceCompany.isPresent()) {
            var updatedInsuranceCompany = insuranceCompanyMapper.updateFromDTO(insuranceCompanyDTO, existingInsuranceCompany.get());
            return insuranceCompanyRepository.save(updatedInsuranceCompany);
        } else {
            var newInsuranceCompany = insuranceCompanyMapper.fromDTO(insuranceCompanyDTO);
            return insuranceCompanyRepository.save(newInsuranceCompany);
        }
    }
    /**
     * This method retrieves an existing InsuranceAgency from the database or creates a new one if it doesn't exist.
     * It first checks if the InsuranceAgencyDTO has an ID. If it does, it tries to find the InsuranceAgency in the database by ID.
     * If the ID is not present or the InsuranceAgency is not found by ID, it then checks if the InsuranceAgencyDTO has a name, address, and country. If it does, it tries to find the InsuranceAgency in the database by these attributes.
     * If the InsuranceAgency is found, it updates the InsuranceAgency with the data from the InsuranceAgencyDTO and saves it to the database.
     * If the InsuranceAgency is not found, it creates a new InsuranceAgency from the InsuranceAgencyDTO and saves it to the database.
     *
     * @param insuranceCertificateDTO The InsuranceCertificateDTO that contains the InsuranceAgencyDTO.
     * @return The existing or newly created InsuranceAgency.
     */
    private InsuranceAgency getOrCreateInsuranceAgency(InsuranceCertificateDTO insuranceCertificateDTO) {
        InsuranceAgencyDTO insuranceAgencyDTO = insuranceCertificateDTO.getInsuranceAgency();
        Optional<InsuranceAgency> existingInsuranceAgency = Optional.empty();

        if (insuranceAgencyDTO.getId() != null) {
            existingInsuranceAgency = insuranceAgencyRepository.findById(insuranceAgencyDTO.getId());
        } else if (insuranceAgencyDTO.getName() != null && insuranceAgencyDTO.getAddress() != null && insuranceAgencyDTO.getCountry() != null) {
            existingInsuranceAgency = insuranceAgencyRepository.findByNameAndAddressAndCountry(insuranceAgencyDTO.getName(), insuranceAgencyDTO.getAddress(), insuranceAgencyDTO.getCountry());
        }

        if (existingInsuranceAgency.isPresent()) {
            var updatedInsuranceAgency = insuranceAgencyMapper.updateFromDTO(insuranceAgencyDTO, existingInsuranceAgency.get());
            return insuranceAgencyRepository.save(updatedInsuranceAgency);
        } else {
            var newInsuranceAgency = insuranceAgencyMapper.fromDTO(insuranceAgencyDTO);
            return insuranceAgencyRepository.save(newInsuranceAgency);
        }
    }
    /**
     * This method retrieves an existing Vehicle from the database or creates a new one if it doesn't exist.
     * It first checks if a Vehicle with the same license plate as the one in the InsuranceCertificateDTO exists in the database.
     * If the Vehicle exists, it checks if the type of the Vehicle (Motor or Trailer) matches the type of the VehicleDTO in the InsuranceCertificateDTO.
     * If the types match, it updates the Vehicle with the data from the VehicleDTO and saves it to the database.
     * If the types don't match, it throws a VehicleMismatchException.
     * If the Vehicle doesn't exist, it creates a new Vehicle from the VehicleDTO and saves it to the database.
     *
     * @param insuranceCertificateDTO The InsuranceCertificateDTO that contains the VehicleDTO.
     * @return The existing or newly created Vehicle, or null if the VehicleDTO is not of type MotorDTO or TrailerDTO.
     * @throws VehicleMismatchException If the type of the existing Vehicle doesn't match the type of the VehicleDTO.
     */
    Vehicle getOrCreateVehicle(InsuranceCertificateDTO insuranceCertificateDTO) throws Exception {
        Optional<Vehicle> existingVehicle = vehicleRepository.findVehicleByLicensePlate(insuranceCertificateDTO.getVehicle().getLicensePlate());
        if (existingVehicle.isPresent()) {
            Vehicle vehicle = existingVehicle.get();
            if (vehicle instanceof Motor) {
                if (insuranceCertificateDTO.getVehicle() instanceof MotorDTO motorDTO) {
                    Motor motor = (Motor) vehicle;
                    var updateMotor = motorMapper.updateFromDTO(motorDTO, motor);
                    return vehicleRepository.save(updateMotor);
                } else {
                    throw new VehicleMismatchException("Vehicle type mismatch: Found a Motor, but DTO contains a different type");
                }
            } else if (vehicle instanceof Trailer) {
                if (insuranceCertificateDTO.getVehicle() instanceof TrailerDTO trailerDTO) {
                    Trailer trailer = (Trailer) vehicle;
                    var updateTrailer = trailerMapper.updateFromDTO(trailerDTO, trailer);
                    return vehicleRepository.save(updateTrailer);
                } else {
                    throw new VehicleMismatchException("Vehicle type mismatch: Found a Trailer, but DTO contains a different type");
                }
            }
        } else {
            VehicleDTO vehicleDTO = insuranceCertificateDTO.getVehicle();
            if (vehicleDTO instanceof MotorDTO motorDTO) {
                Motor newMotor = motorMapper.fromDTO(motorDTO);
                return vehicleRepository.save(newMotor);
            } else if (vehicleDTO instanceof TrailerDTO trailerDTO) {
                Trailer newTrailer = trailerMapper.fromDTO(trailerDTO);
                return vehicleRepository.save(newTrailer);
            }
        }
        return null;
    }

    private void updateInsuranceCertificateOfPolicyHolder(InsuranceCertificateDTO insuranceCertificateDTO, PolicyHolder existingPolicyHolder, InsuranceCompany savedInsuranceCompany, InsuranceAgency savedInsuranceAgency, Vehicle savedVehicle) {
        var insuranceCertificates = existingPolicyHolder.getInsuranceCertificates();
        var certificateToReplace = insuranceCertificates.stream()
                .filter(certificate -> Objects.equals(certificate.getId(), insuranceCertificateDTO.getId()))
                .findFirst();

        if (certificateToReplace.isPresent()) {
            var updatedCertificate = insuranceCertificateMapper.updateFromDTO(insuranceCertificateDTO, certificateToReplace.get());
            insuranceCertificates.remove(certificateToReplace.get());
            insuranceCertificates.add(updatedCertificate);
            updatedCertificate.setInsuranceCompany(savedInsuranceCompany);
            updatedCertificate.setInsuranceAgency(savedInsuranceAgency);
            updatedCertificate.setVehicle(savedVehicle);
        } else {
            var newCertificate = insuranceCertificateMapper.fromDTO(insuranceCertificateDTO);
            newCertificate.setInsuranceCompany(savedInsuranceCompany);
            newCertificate.setInsuranceAgency(savedInsuranceAgency);
            newCertificate.setVehicle(savedVehicle);
            existingPolicyHolder.getInsuranceCertificates().add(newCertificate);
        }
    }

    private void addInsuranceCertificateToPolicyHolder(InsuranceCertificateDTO insuranceCertificateDTO, PolicyHolder existingPolicyHolder, InsuranceCompany savedInsuranceCompany, InsuranceAgency savedInsuranceAgency, Vehicle savedVehicle) {
        var newCertificate = insuranceCertificateMapper.fromDTO(insuranceCertificateDTO);
        newCertificate.setInsuranceCompany(savedInsuranceCompany);
        newCertificate.setInsuranceAgency(savedInsuranceAgency);
        newCertificate.setVehicle(savedVehicle);
        existingPolicyHolder.getInsuranceCertificates().add(newCertificate);
    }

}
