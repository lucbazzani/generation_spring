package com.example.lojagames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lojagames.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public Optional<Usuario> findByUsuario(String usuario); 
	// esses métodos inseridos no repository são métodos de busca criados que não existem no JpaRepository
	//select * from tb_usuarios where usuario = "xxx" | usa-se Optional por permitir receber resposta null	
}
