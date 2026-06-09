package com.humanin.planpaz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// anotações do JPA
@Entity
@Table(name = "users")
// anotações do Lombok
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	// campos da tabela
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String name;
	private String email;
	private String password;
}
