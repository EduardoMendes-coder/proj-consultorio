package br.com.uniamerica.api.repository;

import br.com.uniamerica.api.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

/**
 * @author Eduardo Mendes
 *
 * @since 1.0.0, 31/03/2022
 * @version 1.0.0
 */
@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Modifying
    @Query("UPDATE Agenda agenda " +
            "SET agenda.excluido = :now " +
            "WHERE agenda.id = :agenda")
    public void setUpdateExcluido(@Param("agenda") Long idAgenda, @Param("now") DateTimeFormatter now);

    @Query("select agenda.id from Agenda agenda where agenda.data = :dataAgendamento and agenda.medico = :idMedico")
    public List<Long> listIdPacienteAgenda(@Param("dataAgendamento")LocalDateTime dataAgendamento,
                                           @Param("idMedico") Long idMedico);
}
