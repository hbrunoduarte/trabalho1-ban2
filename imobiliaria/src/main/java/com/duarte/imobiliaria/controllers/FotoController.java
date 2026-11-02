package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.FotoDTO;
import com.duarte.imobiliaria.database.interfaceDAO.FotoDAO;
import com.duarte.imobiliaria.model.Foto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    private final FotoDAO fotoDAO;

    public FotoController(FotoDAO fotoDAO) {
        this.fotoDAO = fotoDAO;
    }

    @GetMapping("/imovel/{idImovel}")
    public ResponseEntity<List<Integer>> listarIdsDoImovel(@PathVariable int idImovel) {
        List<Integer> respostaBanco = fotoDAO.buscarIdsPorImovel(idImovel);
        if (respostaBanco.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(respostaBanco);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FotoDTO> getFoto(@PathVariable int id) {

        Optional<Foto> respostaBanco = fotoDAO.buscarPorId(id);
        if (respostaBanco.isEmpty()) return ResponseEntity.notFound().build();

        Foto foto = respostaBanco.get();
        if (foto.getImagem() == null) return ResponseEntity.noContent().build();

        FotoDTO dto = new FotoDTO();
        dto.setImagem(foto.getImagem());
        dto.setTipo(foto.getTipo());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDeFoto(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("idImovel") Integer idImovel) {

        try {
            if (arquivo.isEmpty()) {
                return ResponseEntity.badRequest().body("Nenhum arquivo foi enviado.");
            }

            Foto novaFoto = new Foto();
            novaFoto.setTipo(arquivo.getContentType()); // Ex: "image/png"
            novaFoto.setImagem(arquivo.getBytes());
            novaFoto.setIdImovel(idImovel);

            fotoDAO.salvar(novaFoto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Foto salva com sucesso! ID: " + novaFoto.getIdFoto());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo recebido.");
        }
    }

}
