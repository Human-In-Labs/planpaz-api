package com.humanin.planpaz.service;

import com.humanin.planpaz.dto.GeoapifyResponseDTO;
import com.humanin.planpaz.dto.LocationResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class LocationService {

    private final RestClient restClient;
    private final String apiKey;

    public LocationService(
            RestClient.Builder restClientBuilder,
            @Value("${geoapify.api.key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.geoapify.com/v1/geocode")
                .build();

        this.apiKey = apiKey;
    }

    public List<LocationResponseDTO> buscar(String busca) {

        GeoapifyResponseDTO response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("text", busca)
                        .queryParam("format", "json")
                        .queryParam("lang", "pt")
                        .queryParam("filter", "countrycode:br")
                        .queryParam("limit", 5)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(GeoapifyResponseDTO.class);

        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results()
                .stream()
                .map(result -> {

                    String bairro = result.suburb();

                    if (
                            bairro == null
                                    && result.name() != null
                                    && (
                                    result.city() == null
                                            || !result.name().equalsIgnoreCase(result.city())
                            )
                    ) {
                        bairro = result.name();
                    }

                    return new LocationResponseDTO(
                            bairro,
                            result.city(),
                            result.state(),
                            result.stateCode(),
                            result.country(),
                            result.countryCode(),
                            result.lat(),
                            result.lon()
                    );
                })
                .toList();
    }
}