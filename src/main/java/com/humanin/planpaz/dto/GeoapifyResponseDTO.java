package com.humanin.planpaz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeoapifyResponseDTO(
        List<Result> results
) {

    public record Result(
            String name,
            String suburb,
            String city,
            String state,

            @JsonProperty("state_code")
            String stateCode,

            String country,

            @JsonProperty("country_code")
            String countryCode,

            double lat,
            double lon
    ) {
    }
}