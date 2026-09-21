package com.humanin.planpaz.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.humanin.planpaz.infra.exception.BusinessException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContentModerationService {

	private final Set<String> forbiddenWords = new HashSet<>();
	private final List<Pattern> forbiddenPatterns = new ArrayList<>();

	@PostConstruct
	public void init() {
		loadForbiddenWords();
	}

	public synchronized void loadForbiddenWords() {
		forbiddenWords.clear();
		forbiddenPatterns.clear();

		List<String> lines = readLinesFromResourcesOrFile();

		for (String rawLine : lines) {
			String line = rawLine.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			// Remove trailing backslashes if present
			if (line.endsWith("\\")) {
				line = line.substring(0, line.length() - 1).trim();
			}

			String[] tokens = line.split(",");
			for (String token : tokens) {
				String cleanToken = token.trim();
				// Strip inline notes like "(em contexto ofensivo)" or "(racista)"
				if (cleanToken.contains("(")) {
					cleanToken = cleanToken.replaceAll("\\(.*?\\)", "").trim();
				}

				if (!cleanToken.isEmpty()) {
					String normalized = normalize(cleanToken);
					if (!normalized.isEmpty()) {
						forbiddenWords.add(normalized);

						// Compile regex word boundary pattern for short words to avoid false positives
						// inside regular words
						String regex = "(?i)(?:^|\\W)" + Pattern.quote(normalized) + "(?:$|\\W)";
						forbiddenPatterns.add(Pattern.compile(regex));
					}
				}
			}
		}

		log.info("[MODERATION] Carregados {} termos proibidos para moderação preventiva.", forbiddenWords.size());
	}

	private List<String> readLinesFromResourcesOrFile() {
		List<String> lines = new ArrayList<>();
		try {
			ClassPathResource resource = new ClassPathResource("listaDePalavras.txt");
			if (resource.exists()) {
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
					String l;
					while ((l = reader.readLine()) != null) {
						lines.add(l);
					}
					return lines;
				}
			}
		} catch (Exception e) {
			log.warn(
					"[MODERATION] Não foi possível ler listaDePalavras.txt do classpath, buscando no sistema de arquivos local.");
		}

		// Fallback to direct file paths
		String[] possiblePaths = { "src/main/resources/listaDePalavras.txt",
				"planpaz-api/src/main/resources/listaDePalavras.txt",
				"src/main/java/com/humanin/planpaz/service/listaDePalavras.txt",
				"planpaz-api/src/main/java/com/humanin/planpaz/service/listaDePalavras.txt", "listaDePalavras.txt" };

		for (String path : possiblePaths) {
			File f = new File(path);
			if (f.exists() && f.isFile()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
					String l;
					while ((l = reader.readLine()) != null) {
						lines.add(l);
					}
					return lines;
				} catch (Exception ex) {
					log.error("[MODERATION] Erro ao ler ficheiro local {}: {}", path, ex.getMessage());
				}
			}
		}

		return lines;
	}

	/**
	 * Valida se o texto contém palavras ou expressões não permitidas. Caso
	 * positivo, lança BusinessException (HTTP 400 Bad Request).
	 */
	public void validateContent(String text) {
		if (text == null || text.isBlank()) {
			return;
		}

		String normalizedText = normalize(text);
		String compactText = removeNonAlphanumeric(normalizedText);

		// 1. Direct search of exact normalized forbidden terms
		for (String word : forbiddenWords) {
			if (word.isBlank())
				continue;

			// Se for uma expressão de múltiplas palavras ou termo longo (>= 4 letras),
			// verifica inclusão no texto normalizado
			if (word.contains(" ") || word.length() >= 4) {
				if (normalizedText.contains(word)) {
					log.warn("[MODERATION] Conteúdo bloqueado contendo termo inadequado: {}", word);
					throw new BusinessException(
							"O conteúdo informado contém termos inadequados ou não permitidos pelas regras da comunidade PlanPaz.");
				}
				// Verifica variação compacta (sem pontuação nem espaços, ex: f.o.d.a -> foda)
				String compactWord = removeNonAlphanumeric(word);
				if (compactWord.length() >= 4 && compactText.contains(compactWord)) {
					log.warn("[MODERATION] Conteúdo bloqueado contendo termo inadequado compactado: {}", word);
					throw new BusinessException(
							"O conteúdo informado contém termos inadequados ou não permitidos pelas regras da comunidade PlanPaz.");
				}
			} else {
				// Para termos muito curtos (ex: cu, krl, prr, fdp), usa limite de palavra (\b)
				// para evitar falsos positivos
				Pattern pattern = Pattern.compile("(?i)(?:^|\\W)" + Pattern.quote(word) + "(?:$|\\W)");
				if (pattern.matcher(normalizedText).find()) {
					log.warn("[MODERATION] Conteúdo bloqueado contendo termo curto inadequado: {}", word);
					throw new BusinessException(
							"O conteúdo informado contém termos inadequados ou não permitidos pelas regras da comunidade PlanPaz.");
				}
			}
		}
	}

	public boolean isForbidden(String text) {
		try {
			validateContent(text);
			return false;
		} catch (BusinessException e) {
			return true;
		}
	}

	private String normalize(String input) {
		if (input == null)
			return "";
		String nfdForm = Normalizer.normalize(input, Normalizer.Form.NFD);
		String stripped = nfdForm.replaceAll("\\p{M}", "");
		return stripped.toLowerCase(Locale.ROOT).trim();
	}

	private String removeNonAlphanumeric(String input) {
		if (input == null)
			return "";
		return input.replaceAll("[^a-z0-9]", "");
	}
}
