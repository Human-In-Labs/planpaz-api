package com.humanin.planpaz.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.humanin.planpaz.config.CacheConfig;
import com.humanin.planpaz.dto.ForecastResponseDTO;
import com.humanin.planpaz.dto.OpenWeatherForecastResponse;
import com.humanin.planpaz.dto.OpenWeatherResponse;
import com.humanin.planpaz.dto.WeatherResponseDTO;
import com.humanin.planpaz.infra.exception.ResourceNotFoundException;

@Service
public class WeatherService {

    private final RestClient restClient;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(4));
        requestFactory.setReadTimeout(Duration.ofSeconds(6));

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .requestFactory(requestFactory)
                .build();
    }

    public static double roundCoord(Double coord) {
        return coord == null ? 0.0 : Math.round(coord * 100.0) / 100.0;
    }

    public static String normalizeCity(String cidade) {
        return cidade != null ? cidade.trim().toLowerCase(Locale.ROOT) : "";
    }

    // ==========================
    // TRAZ O CLIMA ATUAL DA REGIÃO
    // ==========================

    @Cacheable(
            value = CacheConfig.WEATHER_CURRENT_CACHE,
            key = "'coords:' + T(java.lang.Math).round(#latitude * 100.0) / 100.0 + ':' + T(java.lang.Math).round(#longitude * 100.0) / 100.0",
            unless = "#result == null"
    )
    public WeatherResponseDTO getCurrentWeather(
            Double latitude,
            Double longitude
    ) {
        double roundedLat = roundCoord(latitude);
        double roundedLon = roundCoord(longitude);
        String cacheKey = "coords:" + roundedLat + ":" + roundedLon;

        Cache cache = cacheManager != null ? cacheManager.getCache(CacheConfig.WEATHER_CURRENT_CACHE) : null;
        if (cache != null) {
            WeatherResponseDTO cached = cache.get(cacheKey, WeatherResponseDTO.class);
            if (cached != null) {
                return cached;
            }
        }

        try {
            OpenWeatherResponse response = this.restClient
                    .get()
                    .uri(
                            "/weather?lat={lat}&lon={lon}&units=metric&lang=pt_br&appid={apiKey}",
                            roundedLat,
                            roundedLon,
                            apiKey
                    )
                    .retrieve()
                    .body(OpenWeatherResponse.class);

            WeatherResponseDTO dto = mapToWeatherResponseDTO(response);
            if (cache != null && dto != null) {
                cache.put(cacheKey, dto);
            }
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Condições climáticas não encontradas para as coordenadas informadas."
            );
        }
    }

    public WeatherResponseDTO getCurrentWeatherByCoords(Double latitude, Double longitude) {
        return getCurrentWeather(latitude, longitude);
    }

    @Cacheable(
            value = CacheConfig.WEATHER_CURRENT_CACHE,
            key = "'city:' + (#cidade != null ? #cidade.trim().toLowerCase(T(java.util.Locale).ROOT) : '')",
            unless = "#result == null"
    )
    public WeatherResponseDTO getCurrentWeather(String cidade) {
        String normalizedCity = normalizeCity(cidade);
        String cacheKey = "city:" + normalizedCity;

        Cache cache = cacheManager != null ? cacheManager.getCache(CacheConfig.WEATHER_CURRENT_CACHE) : null;
        if (cache != null) {
            WeatherResponseDTO cached = cache.get(cacheKey, WeatherResponseDTO.class);
            if (cached != null) {
                return cached;
            }
        }

        try {
            OpenWeatherResponse response = this.restClient
                    .get()
                    .uri(
                            "/weather?q={cidade}&units=metric&lang=pt_br&appid={apiKey}",
                            cidade != null ? cidade.trim() : "",
                            apiKey
                    )
                    .retrieve()
                    .body(OpenWeatherResponse.class);

            WeatherResponseDTO dto = mapToWeatherResponseDTO(response);
            if (cache != null && dto != null) {
                cache.put(cacheKey, dto);
            }
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Cidade não encontrada: " + cidade
            );
        }
    }

    // ==========================
    // TRAZ O CLIMA DE 3 EM 3 HORAS
    // PARA O PRÓXIMO DIA
    // ==========================

    @Cacheable(
            value = CacheConfig.WEATHER_FORECAST_CACHE,
            key = "'coords:' + T(java.lang.Math).round(#latitude * 100.0) / 100.0 + ':' + T(java.lang.Math).round(#longitude * 100.0) / 100.0",
            unless = "#result == null || #result.isEmpty()"
    )
    @SuppressWarnings("unchecked")
    public List<ForecastResponseDTO> getNextDayForecast(
            Double latitude,
            Double longitude
    ) {
        double roundedLat = roundCoord(latitude);
        double roundedLon = roundCoord(longitude);
        String cacheKey = "coords:" + roundedLat + ":" + roundedLon;

        Cache cache = cacheManager != null ? cacheManager.getCache(CacheConfig.WEATHER_FORECAST_CACHE) : null;
        if (cache != null) {
            List<ForecastResponseDTO> cached = cache.get(cacheKey, List.class);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        }

        try {
            OpenWeatherForecastResponse response = this.restClient
                    .get()
                    .uri(
                            "/forecast?lat={lat}&lon={lon}&cnt=8&units=metric&lang=pt_br&appid={apiKey}",
                            roundedLat,
                            roundedLon,
                            apiKey
                    )
                    .retrieve()
                    .body(OpenWeatherForecastResponse.class);

            List<ForecastResponseDTO> list = mapToForecastResponseDTOList(response);
            if (cache != null && list != null && !list.isEmpty()) {
                cache.put(cacheKey, list);
            }
            return list;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Previsão meteorológica não encontrada para as coordenadas informadas."
            );
        }
    }

    public List<ForecastResponseDTO> getFiveDayForecastByCoords(Double latitude, Double longitude) {
        return getNextDayForecast(latitude, longitude);
    }

    // ==========================
    // OUTRA ASSINATURA PELO MESMO MÉTODO.
    // É PREFERÍVEL USAR LATITUDE E LONGITUDE,
    // POIS A PESQUISA POR CIDADE NÃO É MAIS
    // A FORMA RECOMENDADA PARA ESTE FLUXO.
    // ==========================

    @Cacheable(
            value = CacheConfig.WEATHER_FORECAST_CACHE,
            key = "'city:' + (#cidade != null ? #cidade.trim().toLowerCase(T(java.util.Locale).ROOT) : '')",
            unless = "#result == null || #result.isEmpty()"
    )
    @SuppressWarnings("unchecked")
    public List<ForecastResponseDTO> getNextDayForecast(String cidade) {
        String normalizedCity = normalizeCity(cidade);
        String cacheKey = "city:" + normalizedCity;

        Cache cache = cacheManager != null ? cacheManager.getCache(CacheConfig.WEATHER_FORECAST_CACHE) : null;
        if (cache != null) {
            List<ForecastResponseDTO> cached = cache.get(cacheKey, List.class);
            if (cached != null && !cached.isEmpty()) {
                return cached;
            }
        }

        try {
            OpenWeatherForecastResponse response = this.restClient
                    .get()
                    .uri(
                            "/forecast?q={cidade}&cnt=8&units=metric&lang=pt_br&appid={apiKey}",
                            cidade != null ? cidade.trim() : "",
                            apiKey
                    )
                    .retrieve()
                    .body(OpenWeatherForecastResponse.class);

            List<ForecastResponseDTO> list = mapToForecastResponseDTOList(response);
            if (cache != null && list != null && !list.isEmpty()) {
                cache.put(cacheKey, list);
            }
            return list;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Cidade não encontrada: " + cidade
            );
        }
    }

    public List<ForecastResponseDTO> getFiveDayForecast(String cidade) {
        return getNextDayForecast(cidade);
    }

    // ==========================
    // MÉTODOS PARA ADAPTAR
    // A RESPOSTA DA API DE CLIMA
    // ==========================

    private WeatherResponseDTO mapToWeatherResponseDTO(
            OpenWeatherResponse response
    ) {
        if (response == null || response.main() == null) {
            throw new ResourceNotFoundException(
                    "Dados meteorológicos não encontrados."
            );
        }

        String descricao = "";
        String icone = null;
        boolean isChovendo = false;

        if (
                response.weather() != null
                        && !response.weather().isEmpty()
        ) {
            OpenWeatherResponse.WeatherData weatherData =
                    response.weather().get(0);

            descricao = weatherData.description() != null
                    ? weatherData.description()
                    : "";

            icone = weatherData.icon();

            isChovendo = response.weather()
                    .stream()
                    .anyMatch(weather -> {
                        String main = weather.main();

                        return "Rain".equalsIgnoreCase(main)
                                || "Drizzle".equalsIgnoreCase(main)
                                || "Thunderstorm".equalsIgnoreCase(main);
                    });
        }

        if (!isChovendo && response.rain() != null) {
            if (
                    (
                            response.rain().oneHour() != null
                                    && response.rain().oneHour() > 0
                    )
                            || (
                            response.rain().threeHours() != null
                                    && response.rain().threeHours() > 0
                    )
            ) {
                isChovendo = true;
            }
        }

        Double probabilidadeChuva = null;

        if (response.pop() != null) {
            probabilidadeChuva =
                    Math.round(
                            response.pop()
                                    * 100.0
                                    * 10.0
                    ) / 10.0;
        } else if (isChovendo) {
            probabilidadeChuva = 100.0;
        }

        Double velocidadeVentoKmh = null;

        if (
                response.wind() != null
                        && response.wind().speed() != null
        ) {
            velocidadeVentoKmh =
                    Math.round(
                            response.wind().speed()
                                    * 3.6
                                    * 10.0
                    ) / 10.0;
        }

        String dataHora = response.dt() != null
                ? Instant.ofEpochSecond(response.dt())
                        .atZone(ZoneId.systemDefault())
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        )
                : LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        );

        return new WeatherResponseDTO(
                descricao,
                response.main().temp(),
                response.main().feelsLike(),
                response.main().tempMin(),
                response.main().tempMax(),
                response.main().humidity(),
                velocidadeVentoKmh,
                probabilidadeChuva,
                isChovendo,
                icone,
                response.name(),
                dataHora
        );
    }

    private List<ForecastResponseDTO> mapToForecastResponseDTOList(
            OpenWeatherForecastResponse response
    ) {
        if (
                response == null
                        || response.list() == null
                        || response.list().isEmpty()
        ) {
            throw new ResourceNotFoundException(
                    "Previsão meteorológica não encontrada."
            );
        }

        ZoneId forecastZone = ZoneId.systemDefault();

        if (
                response.city() != null
                        && response.city().timezone() != null
        ) {
            forecastZone = ZoneOffset.ofTotalSeconds(
                    response.city().timezone()
            );
        }

        final ZoneId zone = forecastZone;

        return response.list()
                .stream()
                .map(item -> {

                    String descricao = "";
                    String icone = null;
                    boolean isChovendo = false;

                    if (
                            item.weather() != null
                                    && !item.weather().isEmpty()
                    ) {
                        OpenWeatherForecastResponse.WeatherData weatherData =
                                item.weather().get(0);

                        descricao = weatherData.description() != null
                                ? weatherData.description()
                                : "";

                        icone = weatherData.icon();

                        isChovendo = item.weather()
                                .stream()
                                .anyMatch(weather -> {
                                    String main = weather.main();

                                    return "Rain".equalsIgnoreCase(main)
                                            || "Drizzle".equalsIgnoreCase(main)
                                            || "Thunderstorm".equalsIgnoreCase(main);
                                });
                    }

                    if (
                            !isChovendo
                                    && item.rain() != null
                                    && item.rain().threeHours() != null
                                    && item.rain().threeHours() > 0
                    ) {
                        isChovendo = true;
                    }

                    Double probabilidadeChuva = item.pop() != null
                            ? Math.round(
                                    item.pop() * 100.0 * 10.0
                            ) / 10.0
                            : 0.0;

                    String dataHora = null;
                    String horario = "";

                    if (item.dt() != null) {
                        LocalDateTime dataHoraLocal =
                                LocalDateTime.ofInstant(
                                        Instant.ofEpochSecond(item.dt()),
                                        zone
                                );

                        dataHora = dataHoraLocal.format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        );

                        horario = dataHoraLocal.format(
                                DateTimeFormatter.ofPattern(
                                        "HH:mm"
                                )
                        );

                    } else if (
                            item.dtTxt() != null
                                    && item.dtTxt().length() >= 16
                    ) {
                        dataHora = item.dtTxt();
                        horario = item.dtTxt().substring(11, 16);
                    }

                    Double temp = item.main() != null
                            ? item.main().temp()
                            : null;

                    Double feelsLike = item.main() != null
                            ? item.main().feelsLike()
                            : null;

                    Double tempMin = item.main() != null
                            ? item.main().tempMin()
                            : null;

                    Double tempMax = item.main() != null
                            ? item.main().tempMax()
                            : null;

                    Integer humidity = item.main() != null
                            ? item.main().humidity()
                            : null;

                    return new ForecastResponseDTO(
                            dataHora,
                            horario,
                            descricao,
                            temp,
                            feelsLike,
                            tempMin,
                            tempMax,
                            humidity,
                            probabilidadeChuva,
                            isChovendo,
                            icone
                    );
                })
                .toList();
    }
}
