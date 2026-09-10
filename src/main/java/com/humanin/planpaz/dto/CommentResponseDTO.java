package com.humanin.planpaz.dto;

import java.time.LocalDateTime;
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

	private UUID parentCommentId;

	public static CommentResponseDTO fromEntity(Comment comment) {
		CommentResponseDTO dto = new CommentResponseDTO();
		dto.setId(comment.getId());
		dto.setContent(comment.getContent());
		dto.setCommentedAt(comment.getCommentedAt());

		if (comment.getAuthor() != null) {
			dto.setAuthorId(comment.getAuthor().getId());
			dto.setAuthorName(comment.getAuthor().getName());
			dto.setAuthorUsername(comment.getAuthor().getUsername());
		}

		if (comment.getParentComment() != null) {
			dto.setParentCommentId(comment.getParentComment().getId());
		}

		return dto;
	}
}