package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.humanin.planpaz.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

	private UUID id;
	private String content;
	private LocalDateTime commentedAt;

	private UUID authorId;
	private String authorName;
	private String authorUsername;
	private String authorAvatarUrl;

	private UUID parentCommentId;

	private List<CommentResponseDTO> replies = new ArrayList<>();

	// Conquistas desbloqueadas pela ação
	private AchievementProgressDTO unlockedAchievement;
	private List<AchievementProgressDTO> unlockedAchievements;

	public static CommentResponseDTO fromEntity(Comment comment) {
		CommentResponseDTO dto = new CommentResponseDTO();
		dto.setId(comment.getId());
		dto.setContent(comment.getContent());
		dto.setCommentedAt(comment.getCommentedAt());

		if (comment.getAuthor() != null) {
			dto.setAuthorId(comment.getAuthor().getId());
			dto.setAuthorName(comment.getAuthor().getName());
			dto.setAuthorUsername(comment.getAuthor().getUsername());
			dto.setAuthorAvatarUrl(comment.getAuthor().getAvatarUrl());
		}

		if (comment.getParentComment() != null) {
			dto.setParentCommentId(comment.getParentComment().getId());
		}

		return dto;
	}
}
