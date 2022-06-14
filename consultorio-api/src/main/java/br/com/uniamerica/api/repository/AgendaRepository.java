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
    @Query("UPDATE Agenda agenda SET agenda.ativo = false WHERE agenda.id = :idAgenda")
    public void desativar(@Param("idAgenda") Long idAgenda);

    @Query("select agenda from Agenda agenda where :dataDe between agenda.dataDe and agenda.dataAte " +
            "and :dataAte between agenda.dataDe and agenda.dataAte and agenda.medico = :idMedico " +
            "and agenda.id <> :idAgenda")
    public List<Agenda> findOverlaps(@Param("dataDe") LocalDateTime dataDe,
                                     @Param("dataAte") LocalDateTime dataAte,
                                     @Param("idMedico") Long idMedico,
                                     @Param("idAgenda") Long idAgenda);

    @Query("select agenda from Agenda agenda where :dataDe between agenda.dataDe and agenda.dataAte " +
            "and :dataAte between agenda.dataDe and agenda.dataAte and agenda.paciente = :idPaciente " +
            "and agenda.id <> :idAgenda")
    public List<Agenda> sameTimeAndPatient(@Param("dataDe") LocalDateTime dataDe,
                                           @Param("dataAte") LocalDateTime dataAte,
                                           @Param("idPaciente") Long idPaciente,
                                            @Param("idAgenda") Long idAgenda);
}