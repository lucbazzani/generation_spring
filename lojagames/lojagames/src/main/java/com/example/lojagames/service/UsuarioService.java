package com.example.lojagames.service;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.lojagames.model.Usuario;
import com.example.lojagames.model.UsuarioLogin;
import com.example.lojagames.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired // injeção de dependência
	private UsuarioRepository usuarioRepository;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		// verifica se o usuario passado no argumento existe na base de dados
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Usuário já existe!", null);
		}

		// subtrai a data de hoje pela data de nascimento e devolve a resposta em anos
		int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
		
		if (idade < 18) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário é menor de idade!", null);
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// encriptografa a senha
		String senhaEncoder = encoder.encode(usuario.getSenha());
		usuario.setSenha(senhaEncoder);

		// atualiza a senha do usuario para a senha encriptografada
		return Optional.of(usuarioRepository.save(usuario));
	}

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		// verifica se o usuario passado no argumento existe na base de dados
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			// encriptografa a senha
			String senhaEncoder = encoder.encode(usuario.getSenha());
			usuario.setSenha(senhaEncoder);

			// atualiza a senha do usuario para a senha encriptografada
			return Optional.of(usuarioRepository.save(usuario));
		} 
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!", null);
		}
	} 
	
	private Optional<UsuarioLogin> loginUsuario(Optional <UsuarioLogin> usuarioLogin){
		// verifica se o usuário existe | passa-se o '.get()' porque usuarioLogin e usuario são classes diferentes
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
		
		if (usuario.isPresent()) {
			// compara as senhas
			if(encoder.matches(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {
				// gera o token
				String auth = usuarioLogin.get().getUsuario() + ":" + usuarioLogin.get().getSenha();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);

				// atualiza o objeto usuarioLogin
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setSenha(usuario.get().getSenha());
				usuarioLogin.get().setToken(authHeader);
				
				return usuarioLogin;
			}
		}
		
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou Senha inválidos!", null);		
	}
}
