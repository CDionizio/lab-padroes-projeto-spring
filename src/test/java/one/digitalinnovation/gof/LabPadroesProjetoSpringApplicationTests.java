package one.digitalinnovation.gof;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ViaCepService;

import java.net.URI;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabPadroesProjetoSpringApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @MockitoBean
    private ViaCepService viaCepService;

    @BeforeEach
    void limparClientes() {
        clienteRepository.deleteAll();
        enderecoRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void listarClientesSemCadastrosRetornaListaVazia() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void clientePodeSerCriadoConsultadoAtualizadoEExcluido() throws Exception {
        Endereco endereco = new Endereco();
        endereco.setCep("01001000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setLocalidade("São Paulo");
        endereco.setUf("SP");
        when(viaCepService.consultarCep("01001000")).thenReturn(endereco);

        String location = mockMvc.perform(post("/clientes")
                        .contentType("application/json")
                        .content("""
                                {"nome":"Ana","endereco":{"cep":"01001-000"}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Ana"))
                .andExpect(jsonPath("$.endereco.cep").value("01001000"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String resourcePath = URI.create(java.util.Objects.requireNonNull(location)).getPath();
        Long id = Long.valueOf(resourcePath.substring(resourcePath.lastIndexOf('/') + 1));
        verify(viaCepService).consultarCep("01001000");

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana"));

        mockMvc.perform(put("/clientes/{id}", id)
                        .contentType("application/json")
                        .content("""
                                {"nome":"Ana Silva","endereco":{"cep":"01001000"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Silva"));

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void cepInvalidoRetornaErroDeRequisicao() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType("application/json")
                        .content("""
                                {"nome":"Ana","endereco":{"cep":"123"}}
                                """))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(viaCepService);
    }
}
