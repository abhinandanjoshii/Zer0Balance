package com.consoleadmin.zer0balance.service;

import com.consoleadmin.zer0balance.dto.ProfileDTO;
import com.consoleadmin.zer0balance.entity.ProfileEntity;
import com.consoleadmin.zer0balance.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService  {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // new user
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        // trigger email for user activation
        String activationLink = "https://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Welcome to Zer0Money, Activate your Account Now : ) ";
        String emailBody =
                "Click on the Following Link to activate your Account " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, emailBody);
        return toDTO(newProfile);
    }


    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();

    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
}
