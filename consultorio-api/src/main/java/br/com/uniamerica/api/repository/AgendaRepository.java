package br.com.uniamerica.api.repository;

import br.com.uniamerica.api.entity.Agenda;
import br.com.uniamerica.api.entity.Medico;
import br.com.uniamerica.api.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
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
    public void setUpdateExcluido(@Param("agenda") Long idAgenda, @Param("now") LocalDateTime now);

    @Query("select agenda.paciente from Agenda agenda where agenda.dataDe = :dataAgendamento and agenda.medico = " +
            ":idMedico")
    public List<Paciente> listPacienteAgendados(@Param("dataAgendamento")LocalDateTime dataAgendamento,
                                               @Param("idMedico") Medico idMedico);

    @Query("select agenda from Agenda agenda where :dataDe between agenda.dataDe and agenda.dataAte " +
            "and :dataAte between agenda.dataDe and agenda.dataAte and agenda.medico = :idMedico")
    public List<Agenda> findOverlaps(@Param("dataDe") LocalDateTime dataDe, @Param("dataAte") LocalDateTime dataAte,
                                     @Param("idMedico") Medico idMedico);

    @Query(value = "select date_part('dow', agenda.dataDe), date_part('dow', agenda,dataAte) from Agenda Agenda " +
            "where agenda.id = :idAgenda", nativeQuery = true)
    public List<Integer> checkBusinessDay(@Param("idAgenda") Long idAgenda);
}