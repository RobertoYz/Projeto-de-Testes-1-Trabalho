package com.teste.spring.teste;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.spring.teste.controller.ClienteController;
import com.teste.spring.teste.dto.ClienteDto;
import com.teste.spring.teste.exception.BusinessException;
import com.teste.spring.teste.exception.NotFoundException;
import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.repository.ClienteRepository;
import com.teste.spring.teste.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper; // Para converter objetos Java em JSON

    @MockBean
    ClienteRepository repo;

    @MockBean
    ClienteService service;

    private Cliente clientePadrao;
    private ClienteDto dtoPadrao;

    @BeforeEach
    void setup() {
        clientePadrao = new Cliente();
        clientePadrao.setId(1L);
        clientePadrao.setNome("Ana");
        clientePadrao.setEmail("ana@ex.com");
        clientePadrao.setTelefone("11999990000");

        dtoPadrao = new ClienteDto();
        dtoPadrao.setNome("Ana");
        dtoPadrao.setEmail("ana@ex.com");
        dtoPadrao.setTelefone("11999990000");
    }

    // --- TESTES DE GET (Listar e Buscar por ID) ---

    @Test
    void listar_deveRetornarPaginaDeClientes() throws Exception {
        Page<Cliente> paginaSimulada = new PageImpl<>(
                List.of(clientePadrao),
                PageRequest.of(0, 10, Sort.by("id").ascending()),
                1
        );

        when(repo.findAll(any(Pageable.class))).thenReturn(paginaSimulada);

        mvc.perform(get("/api/clientes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Ana"));
    }

    @Test
    void buscar_deveRetornarCliente_QuandoIdExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(clientePadrao);

        mvc.perform(get("/api/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@ex.com"));
    }

    // --- TESTES DE POST (Criar) ---

    @Test
    void criar_deveRetornarCreated_QuandoDadosValidos() throws Exception {
        when(service.criar(any(Cliente.class))).thenReturn(clientePadrao);

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    void criar_deveRetornarBadRequest_QuandoDadosInvalidos() throws Exception {
        dtoPadrao.setEmail("email-invalido"); // Email inválido

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao)))
                .andExpect(status().isBadRequest()); // Validação @Valid falha
    }

    // --- TESTES DE PUT (Atualizar) ---

    @Test
    void atualizar_deveRetornarDtoAtualizado() throws Exception {
        when(service.atualizar(eq(1L), any(Cliente.class))).thenReturn(clientePadrao);

        mvc.perform(put("/api/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    // --- TESTES DE DELETE (Excluir) ---

    @Test
    void excluir_deveRetornarNoContent() throws Exception {
        doNothing().when(service).excluir(1L);

        mvc.perform(delete("/api/clientes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).excluir(1L);
    }

    // --- TESTES DE EXCEÇÃO (Integration Error Handling) ---

    @Test
    void deveRetornarNotFound_QuandoServiceLancaNotFoundException() throws Exception {
        when(service.buscar(99L)).thenThrow(new NotFoundException("Cliente não encontrado"));

        mvc.perform(get("/api/clientes/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente não encontrado"));
    }

    @Test
    void deveRetornarUnprocessableEntity_QuandoServiceLancaBusinessException() throws Exception {
        // Simulando erro de negócio (ex: email duplicado no update)
        when(service.atualizar(eq(1L), any(Cliente.class))).thenThrow(new BusinessException("Email duplicado"));

        mvc.perform(put("/api/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Email duplicado"));
    }
}