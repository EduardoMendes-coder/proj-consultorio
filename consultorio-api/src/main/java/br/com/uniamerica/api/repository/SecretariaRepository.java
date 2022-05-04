package br.com.uniamerica.api.repository;

import br.com.uniamerica.api.entity.Secretaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Eduardo Mendes
 *
 * @since 1.0.0, 05/04/2022
 * @version 1.0.0
 */
@Repository
public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {
    @Modifying
    @Query("UPDATE Secretaria secretaria " +
            "SET secretaria.excluido = now() " +
            "WHERE secretaria.id = :secretaria")
    public void setUpdateExcluido(@Param("secretaria") Long idSecretaria);
}
