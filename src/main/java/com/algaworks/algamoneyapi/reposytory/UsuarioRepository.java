package com.algaworks.algamoneyapi.reposytory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.algamoneyapi.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	public Optional<Usuario> findByemail(String email);

}
