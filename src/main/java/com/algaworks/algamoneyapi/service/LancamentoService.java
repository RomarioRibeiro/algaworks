package com.algaworks.algamoneyapi.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.algamoneyapi.model.Lancamento;
import com.algaworks.algamoneyapi.model.Pessoa;
import com.algaworks.algamoneyapi.reposytory.LancamentoRepository;
import com.algaworks.algamoneyapi.reposytory.PessoaRepository;
import com.algaworks.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	public Lancamento salvar(Lancamento lancamento) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if (pessoa == null || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) throws IllegalAccessException {
		Lancamento lancamentoSalva = buscarLancamentoExistentePorCodigo(codigo);
		if(!lancamento.getPessoa().equals(buscarLancamentoExistentePorCodigo(codigo))) {
			validarPessoa(lancamento);
		}
		BeanUtils.copyProperties(lancamento, lancamentoSalva, "codigo");
		return lancamentoRepository.save(lancamentoSalva);
	}
	
	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoa = null;
		if(lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		}
		
		if(pessoa == null || pessoa.get().isInativo()) {
			throw new  PessoaInexistenteOuInativaException();
		}
	}

	public Lancamento buscarLancamentoExistentePorCodigo(Long codigo) throws IllegalAccessException {
	return lancamentoRepository.findById(codigo).orElseThrow(() -> new IllegalAccessException());
	}
	
}
