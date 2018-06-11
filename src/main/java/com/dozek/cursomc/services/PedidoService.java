package com.dozek.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozek.cursomc.domain.ItemPedido;
import com.dozek.cursomc.domain.PagamentoComBoleto;
import com.dozek.cursomc.domain.Pedido;
import com.dozek.cursomc.domain.enums.EstadoPagamento;
import com.dozek.cursomc.repositories.ClienteRepository;
import com.dozek.cursomc.repositories.ItemPedidoRepository;
import com.dozek.cursomc.repositories.PagamentoRepository;
import com.dozek.cursomc.repositories.PedidoRepository;
import com.dozek.cursomc.repositories.ProdutoRepository;
import com.dozek.cursomc.services.execeptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	@Autowired
	private BoletoService boletoService;
	@Autowired
	private PagamentoRepository pagamentoRepository;
	@Autowired
	private ProdutoRepository produtoRepository;
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	@Autowired
	private ClienteRepository clienteRepository;
	
	public Pedido find(Integer id) {
		
		Pedido obj=repo.findOne(id);
		if(obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! id" + id
					+", Tipo: "+ Pedido.class.getName());
		}
		return obj;
		
	}
	
	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteRepository.findOne(obj.getCliente().getId()));
		obj.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto,obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for(ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoRepository.findOne(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);			
		}
		itemPedidoRepository.save(obj.getItens());
		System.out.println(obj);
		return obj;
	}

}
