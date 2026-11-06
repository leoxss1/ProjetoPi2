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
import projetoIntegrador.prototico.model.Carrinho;
import projetoIntegrador.prototico.model.Pedido;
import projetoIntegrador.prototico.repository.CarrinhoRepository;

@RestController 
@RequestMapping ("/api/carrinho")
@CrossOrigin (origins = "*")
public class CarrinhoController {
 
    private final CarrinhoRepository repository;

    public CarrinhoController(CarrinhoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Carrinho> listar() {
        return repository.findAll();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<Carrinho> buscarPorId(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                                    .orElse(ResponseEntity
                                    .notFound().build());
    }

    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    public Carrinho cria (@RequestBody @Valid Carrinho carrinho){
        return repository.save(carrinho);
    }

    @PutMapping ("/{id}")
    public Carrinho atualizar(
        @PathVariable Long id,
        @RequestBody @Valid Carrinho carrinho) {

            Carrinho carrinhoExistente = repository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado"));

            carrinhoExistente.setProduto(carrinho.getProduto());
            carrinhoExistente.setQuantidade(carrinho.getQuantidade());
            carrinhoExistente.setPrecoUni(carrinho.getPrecoUni());
            carrinhoExistente.setSubTotal(carrinho.getSubTotal());

            return repository.save(carrinhoExistente);
        }

    @DeleteMapping ("/{id}")
    public void excluir (@PathVariable Long id ){

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Item Não encontrado");
        }

        repository.deleteById(id);
    }

    
}
