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
import projetoIntegrador.prototico.controller.UsuarioController;
import projetoIntegrador.prototico.model.TipoUsuario;
import projetoIntegrador.prototico.model.Usuario;
import projetoIntegrador.prototico.repository.UsuarioRepository;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository repository;

    private static final String JSON_VALIDO = """
            {
              "nome": "Maria Silva",
              "telefone": "11999999999",
              "email": "maria@email.com",
              "senha": "senha123",
              "endereco": "Rua A, 100",
              "tipoUsuario": "CLIENTE"
            }
            """;

    private static final String JSON_SEM_NOME = """
            {
              "nome": "",
              "telefone": "11999999999",
              "email": "maria@email.com",
              "senha": "senha123",
              "endereco": "Rua A, 100",
              "tipoUsuario": "CLIENTE"
            }
            """;

    private Usuario usuario(String nome, String email) {
        return new Usuario(nome, "11999999999", email, "senha123",
                "Rua A, 100", TipoUsuario.CLIENTE);
    }

    @Test
    void listarRetornaUsuarios() throws Exception {
        when(repository.findAll()).thenReturn(List.of(
                usuario("Maria", "maria@email.com"),
                usuario("João", "joao@email.com")));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Maria"))
                .andExpect(jsonPath("$[1].email").value("joao@email.com"));
    }

    @Test
    void buscarPorIdExistenteRetorna200() throws Exception {
        when(repository.findById(1L))
                .thenReturn(Optional.of(usuario("Maria", "maria@email.com")));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.tipoUsuario").value("CLIENTE"));
    }

    @Test
    void buscarPorIdInexistenteRetorna404() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarComDadosValidosRetorna201() throws Exception {
        when(repository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        verify(repository).save(any(Usuario.class));
    }

    @Test
    void criarComDadosInvalidosRetorna400() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_SEM_NOME))
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarExistenteRetorna200ComNovosDados() throws Exception {
        when(repository.findById(1L))
                .thenReturn(Optional.of(usuario("Antigo", "antigo@email.com")));
        when(repository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void atualizarInexistenteRetorna404() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
                .andExpect(status().isNotFound());

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarComDadosInvalidosRetorna400() throws Exception {
        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_SEM_NOME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void excluirExistenteRetorna204() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(1L);
    }

    @Test
    void excluirInexistenteRetorna404() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/99"))
                .andExpect(status().isNotFound());

        verify(repository, never()).deleteById(any());
    }
}
