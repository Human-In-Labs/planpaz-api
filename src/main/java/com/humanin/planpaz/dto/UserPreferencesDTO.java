package com.humanin.planpaz.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.humanin.planpaz.model.enums.ExperienceLevel;
import com.humanin.planpaz.model.enums.Gender;
import com.humanin.planpaz.model.enums.MainGoal;
import com.humanin.planpaz.model.enums.RoomLuminosity;
import com.humanin.planpaz.model.enums.SpaceAvailability;
import com.humanin.planpaz.model.enums.TimeAvailability;

/**
 * DTO para preferências e dados de cultivo/onboarding do usuário.
 */
public record UserPreferencesDTO(
    String bio,
    LocalDate birthdate,
    Gender gender,
    MainGoal mainGoal,
    Set<RoomLuminosity> roomLuminosity,
    Set<SpaceAvailability> spaceAvailability,
    ExperienceLevel experienceLevel,
    TimeAvailability timeAvailability,
    LocalTime wateringTime,
    String city,
    String fcmToken
) {}
