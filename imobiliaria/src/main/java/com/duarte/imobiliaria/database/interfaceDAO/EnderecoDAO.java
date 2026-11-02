package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Endereco;

import java.util.Optional;

public interface EnderecoDAO {
    void salvar(Endereco endereco);
    void inserir(Endereco endereco);
    Optional<Endereco> buscarPorId(int id);
    Optional<Endereco> buscarEndereco(int cep, int numero);
    Optional<Endereco> buscarDadosCep(int cep);
}
