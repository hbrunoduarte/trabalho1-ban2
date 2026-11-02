package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO {
    void salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(int id);
    Optional<Cliente> buscarPorCpf(String cpf);
    List<Cliente> listarTodos();
    boolean excluir(int id);
}