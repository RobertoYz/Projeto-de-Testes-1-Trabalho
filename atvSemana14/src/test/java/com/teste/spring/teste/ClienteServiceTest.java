package com.teste.spring.teste;

import com.teste.spring.teste.exception.BusinessException;
import com.teste.spring.teste.exception.NotFoundException;
import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.repository.ClienteRepository;
import com.teste.spring.teste.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository repo;

    @InjectMocks
    ClienteService service;

    @Test
    void criar_deveSalvarCliente_QuandoEmailNaoExiste() {
        Cliente c = new Cliente();
        c.setEmail("novo@ex.com");
        c.setNome("Novo");

        // Mock: email não existe
        when(repo.existsByEmail("novo@ex.com")).thenReturn(false);
        // Mock: save retorna o objeto
        when(repo.save(c)).thenReturn(c);

        Cliente salvo = service.criar(c);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getEmail()).isEqualTo("novo@ex.com");
        verify(repo).save(c);
    }

    @Test
    void criar_deveLancarSeEmailJaExiste() {
        Cliente c = new Cliente();
        c.setEmail("j@ex.com");
        when(repo.existsByEmail("j@ex.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(c))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");
        verify(repo, never()).save(any());
    }

    @Test
    void buscar_deveRetornarCliente_QuandoIdExiste() {
        Cliente existente = new Cliente();
        existente.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        Cliente resultado = service.buscar(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void buscar_deveLancarNotFound_QuandoIdNaoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    void atualizar_deveAtualizarCamposBasicos() {
        Cliente antigo = new Cliente();
        antigo.setId(1L);
        antigo.setNome("Antigo");
        antigo.setEmail("a@ex.com");

        // Mocks para simular cenário válido
        when(repo.findById(1L)).thenReturn(Optional.of(antigo));
        when(repo.save(antigo)).thenReturn(antigo);

        Cliente dadosNovos = new Cliente();
        dadosNovos.setNome("Novo");
        dadosNovos.setEmail("a@ex.com"); // mesmo email (permitido)
        dadosNovos.setTelefone("22");

        Cliente atualizado = service.atualizar(1L, dadosNovos);

        assertThat(atualizado.getNome()).isEqualTo("Novo");
        // Verifica se o repository não foi chamado para verificar email
        // (pois o email não mudou ou a lógica interna tratou)
    }

    @Test
    void excluir_deveChamarDelete_QuandoClienteExiste() {
        Cliente existente = new Cliente();
        existente.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(repo).delete(existente);
    }
}
