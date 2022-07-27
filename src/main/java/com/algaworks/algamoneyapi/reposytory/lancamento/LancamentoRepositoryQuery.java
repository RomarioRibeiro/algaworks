package com.algaworks.algamoneyapi.reposytory.lancamento;

import java.util.List;

import com.algaworks.algamoneyapi.model.Lancamento;
import com.algaworks.algamoneyapi.reposytory.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {

	public List<Lancamento> filtar(LancamentoFilter lancamentoFilter);
}
