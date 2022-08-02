package com.algaworks.algamoneyapi.resource;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algamoneyapi.event.RecursoCriadoEvent;
import com.algaworks.algamoneyapi.exceptionhandler.AlgamoneyExceptionHandler.Erro;
import com.algaworks.algamoneyapi.model.Lancamento;
import com.algaworks.algamoneyapi.reposytory.LancamentoRepository;
import com.algaworks.algamoneyapi.reposytory.filter.LancamentoFilter;
import com.algaworks.algamoneyapi.reposytory.projection.ResumoLancamento;
import com.algaworks.algamoneyapi.service.LancamentoService;
import com.algaworks.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
	public Page<Lancamento> listar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return   lancamentoRepository.filtar(lancamentoFilter, pageable);
					
				
	}
	
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return   lancamentoRepository.resumir(lancamentoFilter, pageable);
					
				
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
	public ResponseEntity<Lancamento> buscarPorCodigo(@PathVariable Long codigo) {
		return lancamentoRepository.findById(codigo)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
		
		
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_wrile')")
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
		
		applicationEventPublisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
		
	}
	
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and hasAuthority('SCOPE_wrile')")
	public void remover(@PathVariable Long codigo) {
		this.lancamentoRepository.deleteById(codigo);
	}
	
	
	@ExceptionHandler({ PessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}
	
}
