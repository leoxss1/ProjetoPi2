package projetoIntegrador.prototico;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import projetoIntegrador.prototico.SecurityConfig.SecurityConfig;
import projetoIntegrador.prototico.controller.ProdutoController;
import projetoIntegrador.prototico.model.Produto;
import projetoIntegrador.prototico.model.TipoProduto;
import projetoIntegrador.prototico.repository.ProdutoRepository;

@WebMvcTest(ProdutoController.class)
@Import(SecurityConfig.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoRepository repository;

    private static final String JSON_VALIDO = """
            {
              "nome": "Coxinha",
              "preco": 7.5,
              "descricao": "Coxinha de frango",
              "quantidade": 20,
              "dataValidade": "2099-12-31",
              "tipoProduto": "SALGADO"
            }
            """;

    private static final String JSON_SEM_PRECO = """
            {
              "nome": "Coxinha",
              "descricao": "Coxinha de frango",
              "quantidade": 20,
              "dataValidade": "2099-12-31",
              "tipoProduto": "SALGADO"
            }
            """;

    private static final String JSON_VALIDADE_PASSADA = """
            {
              "nome": "Coxinha",
              "preco": 7.5,
              "descricao": "Coxinha de frango",
              "quantidade": 20,
              "dataValidade": "2020-01-01",
              "tipoProduto": "SALGADO"
            }
            """;

    private Produto produto(String nome, double preco, TipoProduto tipo) {
        return new Produto(nome, preco, "Descrição de " + nome, 10,
                LocalDate.now().plusDays(7), tipo);
    }

    @Test
    void listarRetornaProdutos() throws Exception {
        when(repository.findAll()).thenReturn(List.of(
                produto("Coxinha", 7.5, TipoProduto.SALGADO),
                produto("Brigadeiro", 3.0, TipoProduto.DOCE)));

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Coxinha"))
                .andExpect(jsonPath("$[1].tipoProduto").value("DOCE"));
    }

    @Test
    void buscarPorIdExistenteRetorna200() throws Exception {
        when(repository.findById(1L))
                .thenReturn(Optional.of(produto("Coxinha", 7.5, TipoProduto.SALGADO)));

        mockMvc.perform(get("/api/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Coxinha"))
                .andExpect(jsonPath("$.preco").value(7.5));
    }

    @Test
    void buscarPorIdInexistenteRetorna404() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/produtos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarComDadosValidosRetorna201() throws Exception {
        when(repository.save(any(Produto.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Coxinha"))
                .andExpect(jsonPath("$.quantidade").value(20))
                .andExpect(jsonPath("$.tipoProduto").value("SALGADO"));

        verify(repository).save(any(Produto.class));
    }

    @Test
    void criarSemPrecoRetorna400() throws Exception {
        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_SEM_PRECO))
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void criarComValidadeNoPassadoRetorna400() throws Exception {
        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDADE_PASSADA))
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void atualizarExistenteRetorna200ComNovosDados() throws Exception {
        when(repository.findById(1L))
                .thenReturn(Optional.of(produto("Antigo", 1.0, TipoProduto.DOCE)));
        when(repository.save(any(Produto.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        mockMvc.perform(put("/api/produtos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Coxinha"))
                .andExpect(jsonPath("$.preco").value(7.5))
                .andExpect(jsonPath("$.tipoProduto").value("SALGADO"));
    }

    @Test
    void atualizarInexistenteRetorna404() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/produtos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
                .andExpect(status().isNotFound());

        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    void excluirExistenteRetorna204() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/produtos/1"))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(1L);
    }

    @Test
    void excluirInexistenteRetorna404() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/produtos/99"))
                .andExpect(status().isNotFound());

        verify(repository, never()).deleteById(any());
    }
}
