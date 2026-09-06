package com.humanin.planpaz.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.humanin.planpaz.model.User;
import com.humanin.planpaz.model.enums.ExperienceLevel;
import com.humanin.planpaz.model.enums.Gender;
import com.humanin.planpaz.model.enums.MainGoal;
import com.humanin.planpaz.model.enums.RoomLuminosity;
import com.humanin.planpaz.model.enums.SpaceAvailability;
import com.humanin.planpaz.model.enums.TimeAvailability;

/**
 * DTO completo para tela de perfil e configurações do usuário.
 */
public record UserSettingsDTO(
    UUID id,
    String name,
    String username,
    String email,
    String bio,
    LocalDate birthdate,
    Gender gender,
    MainGoal mainGoal,
    Set<RoomLuminosity> roomLuminosity,
    Set<SpaceAvailability> spaceAvailability,
    ExperienceLevel experienceLevel,
    TimeAvailability timeAvailability,
    LocalTime wateringTime,
    Integer ecoscore,
    String city,
    String fcmToken,
    LocalDateTime createdAt
) {
    public static UserSettingsDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserSettingsDTO(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getBio(),
            user.getBirthdate(),
            user.getGender(),
            user.getMainGoal(),
            user.getRoomLuminosity(),
            user.getSpaceAvailability(),
            user.getExperienceLevel(),
            user.getTimeAvailability(),
            user.getWateringTime(),
            user.getEcoscore(),
            user.getCity(),
            user.getFcmToken(),
            user.getCreatedAt()
        );
    }
}
