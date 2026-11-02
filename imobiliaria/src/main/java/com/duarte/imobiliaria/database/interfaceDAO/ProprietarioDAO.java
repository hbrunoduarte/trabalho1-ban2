package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Proprietario;

import java.util.List;
import java.util.Optional;

public interface ProprietarioDAO {
    void salvar(Proprietario proprietario);
    void atualizarEndereco(Integer idProprietario, Integer idEndereco);
    Optional<Proprietario> buscarPorId(int id);
    Optional<Proprietario> buscarPorCpf(String cpf);
    Optional<Proprietario> buscarPorCnpj(String cnpj);
    List<Proprietario> listarTodos();
    boolean excluir(int id);
}
