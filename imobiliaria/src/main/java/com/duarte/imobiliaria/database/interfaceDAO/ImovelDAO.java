package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Imovel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ImovelDAO {

    String STATUS_VENDA = "disponível para venda";
    String STATUS_LOCACAO = "disponível para locação";

    void salvar(Imovel imovel);
    Optional<Imovel> buscarPorId(int id);
    Optional<Imovel> buscarPorEndereco(int cep, int numero);
    List<Imovel> buscarPorProprietario(int idProprietario);
    Optional<Float> buscarMaiorPreco(String tipoPublicacao) throws SQLException;
    Optional<List<Imovel>> buscarPorTipo(int idTipoImovel);
    List<Imovel> listarTodos();
    boolean excluir(int id);
}
