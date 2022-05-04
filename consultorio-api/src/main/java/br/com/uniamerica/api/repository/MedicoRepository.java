package br.com.uniamerica.api.repository;

import br.com.uniamerica.api.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Eduardo Mendes
 *
 * @since 1.0.0, 22/03/2022
 * @version 1.0.0
 *
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Modifying
    @Query("UPDATE Medico medico " +
            "SET medico.excluido = now() " +
            "WHERE medico.id = :medico")
    public void setUpdateExcluido(@Param("medico") Long idMedico);

    /**
     * @see Medico#Medico(Long, String, String)
     */
    @Query("SELECT new Medico(medico.id, medico.nome, medico.crm) FROM Medico medico")
    public List<Medico> listTable();
}
