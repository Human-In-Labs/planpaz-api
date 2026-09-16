package com.humanin.planpaz.infra.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

	private static final String SPLIT_CHAR = ",";

	@Override
	public String convertToDatabaseColumn(List<String> stringList) {
		if (stringList == null || stringList.isEmpty()) {
			return "";
		}
		return String.join(SPLIT_CHAR, stringList.stream()
				.filter(s -> s != null && !s.isBlank())
				.map(s -> s.replace(SPLIT_CHAR, "").trim())
				.toList());
	}

	@Override
	public List<String> convertToEntityAttribute(String string) {
		if (string == null || string.isBlank()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(Arrays.stream(string.split(SPLIT_CHAR))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList());
	}
}
