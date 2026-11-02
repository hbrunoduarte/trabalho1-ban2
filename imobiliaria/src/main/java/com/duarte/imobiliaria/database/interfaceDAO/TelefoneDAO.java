package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.TelefoneCliente;
import com.duarte.imobiliaria.model.TelefoneFiador;
import com.duarte.imobiliaria.model.TelefoneProprietario;

import java.util.List;

public interface TelefoneDAO {
    void salvarDoCliente(List<TelefoneCliente> telefone);
    void salvarDoProprietario(List<TelefoneProprietario> telefone);
    void salvarDoFiador(List<TelefoneFiador> telefone);
    boolean excluirDoCliente(int idTelefone);
    boolean excluirDoProprietario(int idTelefone);
    boolean excluirDoFiador(int idTelefone);
    List<TelefoneCliente> listarDoCliente(int idCliente);
    List<TelefoneProprietario> listarDoProprietario(int idProprietario);
    List<TelefoneProprietario> buscarPorIdImovel(int idImovel);
    List<TelefoneFiador> listarDoFiador(int idFiador);
}
