package projetoIntegrador.prototico.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import projetoIntegrador.prototico.dto.PagamentoRequest;
import projetoIntegrador.prototico.model.Pagamento;
import projetoIntegrador.prototico.model.Pedido;
import projetoIntegrador.prototico.model.TipoStatusPagamento;
import projetoIntegrador.prototico.repository.PagamentoRepository;
import projetoIntegrador.prototico.repository.PedidoRepository;

@Service
public class PagamentoService {

    @Autowired 
    private PagamentoRepository repository;

    // Cadastrar pagamento
    public Pagamento cadastrar(Pagamento pagamento) {
        return repository.save(pagamento);
    }

    // Listar todos os pagamentos
    public List<Pagamento> listar() {
        return repository.findAll();
    }

    // Buscar pagamento por ID
    public Pagamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pagamento não encontrado"));
    }

    // Buscar pagamento pelo pedido
    public Pagamento buscarPorPedido(Long pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pagamento não encontrado para este pedido"));
    }

    // Atualizar pagamento
    public Pagamento atualizar(Long id, Pagamento pagamento) {

        Pagamento pagamentoExistente = buscarPorId(id);

        pagamentoExistente.setValor(pagamento.getValor());
        pagamentoExistente.setTipoPagamento(pagamento.getTipoPagamento());
        pagamentoExistente.setStatus(pagamento.getStatus());
        pagamentoExistente.setDataPagamento(pagamento.getDataPagamento());

        return repository.save(pagamentoExistente);
    }

    // Excluir pagamento
    public void excluir(Long id) {
        Pagamento pagamento = buscarPorId(id);
        repository.delete(pagamento);
    }
}