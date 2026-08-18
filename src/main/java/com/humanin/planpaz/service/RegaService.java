package com.humanin.planpaz.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.humanin.planpaz.dto.ClimaResponseDTO;
import com.humanin.planpaz.model.GardenPlant;

@Service
public class RegaService {

	public String calcularProximaRega(GardenPlant planta, ClimaResponseDTO clima) {
		// Regra 1: Se estiver chovendo, adia a rega para o dia seguinte
		if (clima.isChovendo()) {
			planta.setLastWatering(LocalDate.now()); // Considera "regada" pela chuva
			return "Chuva detectada na região. A rega foi adiada para amanhã!";
		}

		// Regra 2: Umidade muito alta (acima de 80%) diminui a necessidade de rega
		// imediata
		if (clima.getUmidade() > 80) {
			return "Umidade do ar alta (" + clima.getUmidade() + "%). Não é necessário regar hoje.";
		}

		// Regra 3: Dias muito quentes (acima de 30°C) e secos aceleram a necessidade de
		// rega
		if (clima.getTemperatura() > 30.0 && clima.getUmidade() < 40) {
			return "Alerta de calor e ar seco! Recomendado regar hoje no final da tarde.";
		}

		return "Condições normais. Siga o cronograma padrão da planta.";
	}
}