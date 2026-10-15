package com.humanin.planpaz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.humanin.planpaz.model.PlantStage;

class GardenPlantServiceStageSuggestionTest {

	private static PlantStage stage(Integer days) {
		PlantStage s = new PlantStage();
		s.setDays(days);
		return s;
	}

	// Estágios: [0,10) [10,30) [30,60) e depois o último continua
	private final List<PlantStage> estagios = List.of(stage(10), stage(20), stage(30));

	@Test
	void listaVaziaRetornaMenosUm() {
		assertEquals(-1, GardenPlantService.calcularIndiceEstagioIdeal(List.of(), 5));
	}

	@Test
	void diaZeroEstaNoPrimeiroEstagio() {
		assertEquals(0, GardenPlantService.calcularIndiceEstagioIdeal(estagios, 0));
	}

	@Test
	void ultimoDiaDoPrimeiroEstagio() {
		assertEquals(0, GardenPlantService.calcularIndiceEstagioIdeal(estagios, 9));
	}

	@Test
	void limiteExatoAvancaParaProximo() {
		assertEquals(1, GardenPlantService.calcularIndiceEstagioIdeal(estagios, 10));
		assertEquals(2, GardenPlantService.calcularIndiceEstagioIdeal(estagios, 30));
	}

	@Test
	void alemDoTotalPermaneceNoUltimo() {
		assertEquals(2, GardenPlantService.calcularIndiceEstagioIdeal(estagios, 500));
	}

	@Test
	void diasNuloEhTerminal() {
		List<PlantStage> comNulo = List.of(stage(10), stage(null), stage(30));
		assertEquals(1, GardenPlantService.calcularIndiceEstagioIdeal(comNulo, 500));
	}
}
