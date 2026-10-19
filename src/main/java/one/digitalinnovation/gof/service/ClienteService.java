package one.digitalinnovation.gof.service;

import one.digitalinnovation.gof.model.Cliente;

import java.util.List;

public interface ClienteService {

    List<Cliente> buscarTodos();

    Cliente buscarPorId(Long id);

    Cliente inserir(Cliente cliente);

    Cliente atualizar(Long id, Cliente cliente);

    void deletar(Long id);
}
