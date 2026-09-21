package com.humanin.planpaz.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.humanin.planpaz.infra.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public String uploadImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException("O arquivo de imagem enviado está vazio.");
		}
		try {
			Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("resource_type", "auto"));
			String secureUrl = (String) uploadResult.get("secure_url");
			if (secureUrl == null || secureUrl.isBlank()) {
				secureUrl = (String) uploadResult.get("url");
			}
			return secureUrl;
		} catch (IOException e) {
			throw new BusinessException("Falha ao realizar upload para o Cloudinary: " + e.getMessage());
		}
	}
}
