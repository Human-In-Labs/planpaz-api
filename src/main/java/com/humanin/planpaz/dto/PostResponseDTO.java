package com.humanin.planpaz.dto;

import com.humanin.planpaz.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {

	private UUID id;
	private String title;
	private String content;
	private String media;
	private LocalDateTime postedAt;

	// Autor
	private UUID authorId;
	private String authorName;
	private String authorUsername;

	// Métricas
	private long likesCount;
	private boolean likedByCurrentUser;
	private long commentsCount;

	public static PostResponseDTO fromEntity(Post post, long likesCount, boolean likedByCurrentUser,
			long commentsCount) {
		PostResponseDTO dto = new PostResponseDTO();
		dto.setId(post.getId());
		dto.setTitle(post.getTitle());
		dto.setContent(post.getContent());
		dto.setMedia(post.getMedia());
		dto.setPostedAt(post.getPostedAt());

		if (post.getAuthor() != null) {
			dto.setAuthorId(post.getAuthor().getId());
			dto.setAuthorName(post.getAuthor().getName());
			dto.setAuthorUsername(post.getAuthor().getUsername());
		}

		dto.setLikesCount(likesCount);
		dto.setLikedByCurrentUser(likedByCurrentUser);
		dto.setCommentsCount(commentsCount);

		return dto;
	}
}