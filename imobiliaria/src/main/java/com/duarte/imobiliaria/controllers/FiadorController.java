package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.database.interfaceDAO.FiadorDAO;
import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Fiador;
import com.duarte.imobiliaria.model.TelefoneFiador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fiadores")
public class FiadorController {

    private final FiadorDAO fiadorDAO;
    private final TelefoneDAO telefoneDAO;

    public FiadorController(FiadorDAO fiadorDAO, TelefoneDAO telefoneDAO) {
        this.fiadorDAO = fiadorDAO;
        this.telefoneDAO = telefoneDAO;
    }

    public static class FiadorDTO {
        public String cpf;
        public String nome;
        public byte[] comprovanteRenda;
        public Integer idEndereco;
        public List<String> telefones;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody FiadorController.FiadorDTO dto) {

        Fiador fiador = new Fiador();
        fiador.setCpf(dto.cpf);
        fiador.setNome(dto.nome);
        fiador.setComprovanteRenda(dto.comprovanteRenda);
        fiador.setEndereco(new Endereco(dto.idEndereco));

        fiadorDAO.salvar(fiador);
        telefoneDAO.salvarDoFiador(dto.telefones.stream()
                                    .map(t -> new TelefoneFiador(t, fiador)).toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(fiador);
    }

}
