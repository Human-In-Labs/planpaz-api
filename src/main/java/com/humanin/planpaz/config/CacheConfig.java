package com.humanin.planpaz.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
@EnableAsync
public class CacheConfig {

	public static final String WEATHER_CURRENT_CACHE = "weather_current";
	public static final String WEATHER_FORECAST_CACHE = "weather_forecast";
	public static final String GEO_AUTOCOMPLETE_CACHE = "geo_autocomplete";
	public static final String PLANT_CATALOG_CACHE = "plant_catalog";
	public static final String PLANT_STAGES_CACHE = "plant_stages";
	public static final String PLANT_CARE_CACHE = "plant_care";
	public static final String ACHIEVEMENTS_DEF_CACHE = "achievements_def";
	public static final String AUTH_PRINCIPAL_CACHE = "auth_principal";

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		cacheManager.registerCustomCache(WEATHER_CURRENT_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(15, TimeUnit.MINUTES)
						.maximumSize(2000)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(WEATHER_FORECAST_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(60, TimeUnit.MINUTES)
						.maximumSize(2000)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(GEO_AUTOCOMPLETE_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(24, TimeUnit.HOURS)
						.maximumSize(5000)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(PLANT_CATALOG_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(12, TimeUnit.HOURS)
						.maximumSize(200)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(PLANT_STAGES_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(12, TimeUnit.HOURS)
						.maximumSize(200)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(PLANT_CARE_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(12, TimeUnit.HOURS)
						.maximumSize(200)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(ACHIEVEMENTS_DEF_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(24, TimeUnit.HOURS)
						.maximumSize(50)
						.recordStats()
						.build());

		cacheManager.registerCustomCache(AUTH_PRINCIPAL_CACHE,
				Caffeine.newBuilder()
						.expireAfterWrite(60, TimeUnit.SECONDS)
						.maximumSize(10000)
						.recordStats()
						.build());

		return cacheManager;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofSeconds(4))
				.setReadTimeout(Duration.ofSeconds(6))
				.build();
	}
}
