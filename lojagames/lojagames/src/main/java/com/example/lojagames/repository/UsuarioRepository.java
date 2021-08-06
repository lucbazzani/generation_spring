package com.example.lojagames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lojagames.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public Optional<Usuario> findByUsuario(String usuario); 
	// esses métodos inseridos no repository são métodos de busca criados que não existem no JpaRepository
	//select * from tb_usuarios where usuario = "xxx" | usa-se Optional por permitir receber resposta null	
}
