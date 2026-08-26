package com.humanin.planpaz.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//anotações do JPA
@Entity
@Table(name = "comment")
//anotações do Lombok
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
	// campos da tabela
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;
	
	private String content;
	private LocalDateTime commentedAt;
}
