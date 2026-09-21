package com.humanin.planpaz.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.humanin.planpaz.service.CloudinaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

	private final CloudinaryService cloudinaryService;

	@PostMapping
	public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
		String imageUrl = cloudinaryService.uploadImage(file);
		return ResponseEntity.ok(Map.of("url", imageUrl));
	}
}
