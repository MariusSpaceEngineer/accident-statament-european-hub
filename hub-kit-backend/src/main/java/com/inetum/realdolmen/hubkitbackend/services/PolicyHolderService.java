package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.exceptions.VehicleMismatchException;
import com.inetum.realdolmen.hubkitbackend.mappers.*;
import com.inetum.realdolmen.hubkitbackend.models.*;
import com.inetum.realdolmen.hubkitbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
            return Optional.empty(); // User not found
        }
    }


    public Optional<List<InsuranceCertificateDTO>> updateInsuranceCertificateInformation(String token, InsuranceCertificateDTO insuranceCertificateDTO) throws Exception {

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

    }

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

    private Optional<User> getUser(String token) {
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
     * @param dto The policyholder DTO to which the vehicle DTOs are added.
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

    private Vehicle getOrCreateVehicle(InsuranceCertificateDTO insuranceCertificateDTO) throws Exception {
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
