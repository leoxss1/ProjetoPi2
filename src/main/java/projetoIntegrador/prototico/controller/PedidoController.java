package projetoIntegrador.prototico.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import projetoIntegrador.prototico.model.Pedido;
import projetoIntegrador.prototico.repository.PedidoRepository;

@RestController 
@RequestMapping ("/api/pedidos")
@CrossOrigin (origins = "*")
public class PedidoController {

    private final PedidoRepository repository;

    public PedidoController(PedidoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Pedido> listar() {
        return repository.findAll();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                                    .orElse(ResponseEntity
                                    .notFound().build());
    }

    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    public Pedido cria (@RequestBody @Valid Pedido pedido){
        return repository.save(pedido);
    }

    @PutMapping ("/{id}")
    public Pedido atualizar(
        @PathVariable Long id,
        @RequestBody @Valid Pedido pedido) {

            Pedido pedidoExistente = repository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado"));

            pedidoExistente.setNomeCliente(pedido.getNomeCliente());;
            pedidoExistente.setQuantidade(pedido.getQuantidade());
            pedidoExistente.setEndereco(pedido.getEndereco());
            pedidoExistente.setValorPedido(pedido.getValorPedido());

            return repository.save(pedidoExistente);
        }

    @DeleteMapping ("/{id}")
    public void excluir (@PathVariable Long id ){

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pedido Não encontrado");
        }

        repository.deleteById(id);
    }
}
