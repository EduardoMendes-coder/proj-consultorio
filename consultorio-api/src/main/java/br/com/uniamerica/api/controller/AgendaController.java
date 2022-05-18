package br.com.uniamerica.api.controller;

import br.com.uniamerica.api.entity.Agenda;
import br.com.uniamerica.api.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Eduardo Mendes
 *
 * @since 1.0.0, 07/04/2022
 * @version 1.0.0
 */
@Controller
@RequestMapping("/api/agendas")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping("/{idAgenda}")
    public ResponseEntity<Agenda> findById(
            @PathVariable("idAgenda") Long idAgenda
    ){
        return ResponseEntity.ok().body(this.agendaService.findById(idAgenda).get());
    }

    @GetMapping
    public ResponseEntity<Page<Agenda>> listByAllPage(
            Pageable pageable
    ){
        return ResponseEntity.ok().body(this.agendaService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<?> insert(
            @RequestBody Agenda agenda
    ){
        try {
            this.agendaService.insert(agenda);
            return ResponseEntity.ok().body("Agenda Cadastrada com Sucesso.");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{idAgenda}")
    public ResponseEntity<?> update(
            @RequestBody Agenda agenda,
            @PathVariable Long idAgenda
    ){
        try {
            this.agendaService.update(idAgenda, agenda);
            return ResponseEntity.ok().body("Agenda Atualizada com Sucesso.");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/status/{idAgenda}")
    public ResponseEntity<?> updateStatus(
            @RequestBody Agenda agenda,
            @PathVariable Long idAgenda
    ){
        try {
            this.agendaService.updateStatus(idAgenda, agenda);
            return ResponseEntity.ok().body("Agenda Desativada com Sucesso.");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
