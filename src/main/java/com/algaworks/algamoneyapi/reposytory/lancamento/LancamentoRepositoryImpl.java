package com.algaworks.algamoneyapi.reposytory.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.algaworks.algamoneyapi.model.Categoria_;
import com.algaworks.algamoneyapi.model.Lancamento;
import com.algaworks.algamoneyapi.model.Lancamento_;
import com.algaworks.algamoneyapi.model.Pessoa_;
import com.algaworks.algamoneyapi.reposytory.filter.LancamentoFilter;
import com.algaworks.algamoneyapi.reposytory.projection.ResumoLancamento;

public class LancamentoRepositoryImpl  implements LancamentoRepositoryQuery{
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Override
	public Page<Lancamento> filtar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		//criar restrições
		Predicate[] predicate = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicate);
		
		
		TypedQuery<Lancamento> query = entityManager.createQuery(criteria);
		adicionarRegistracoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter)) ;
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = criteriaBuilder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(criteriaBuilder.construct(ResumoLancamento.class
				, root.get(Lancamento_.codigo), root.get(Lancamento_.descricao)
				, root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento)
				, root.get(Lancamento_.valor), root.get(Lancamento_.tipo)
				, root.get(Lancamento_.categoria).get(Categoria_.nome)
				, root.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		
		TypedQuery<ResumoLancamento> query = entityManager.createQuery(criteria);
		adicionarRegistracoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter)) ;
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> pridicates = new ArrayList<>();
 		
		if(!ObjectUtils.isEmpty(lancamentoFilter.getDescricao())) {
			pridicates.add(builder.like(builder.lower(root.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}
		if(lancamentoFilter.getDataVencimentoDe() != null) {
			pridicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
		}
		if(lancamentoFilter.getDataVencimentoAte() != null) {
			pridicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
		}
		
		
		return pridicates.toArray(new Predicate[pridicates.size()]);
	}

	private void adicionarRegistracoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina= paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
		
	}
	

	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicate = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicate);
		
		criteria.select(builder.count(root));
		return entityManager.createQuery(criteria).getSingleResult();
	}


}
