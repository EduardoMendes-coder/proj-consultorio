package br.com.uniamerica.api.service;

import br.com.uniamerica.api.entity.Especialidade;
import br.com.uniamerica.api.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author Eduardo Mendes
 *
 * @since 1.0.0, 27/04/2022
 * @version 1.0.0
 */
@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    /**
     *
     * @param id
     * @return
     */
    public Optional<Especialidade> findById(Long id){
        return this.especialidadeRepository.findById(id);
    }

    /**
     *
     * @param pageable
     * @return
     */
    public Page<Especialidade> listAll(Pageable pageable){
        return this.especialidadeRepository.findAll(pageable);
    }

    /**
     *
     * @param id
     * @param especialidade
     */
    @Transactional
    public void update(Long id, Especialidade especialidade){
        if (id == especialidade.getId()) {
            this.especialidadeRepository.save(especialidade);
        }
        else {
            throw new RuntimeException();
        }
    }

    /**
     *
     * @param especialidade
     */
    @Transactional
    public void insert(Especialidade especialidade){
        this.especialidadeRepository.save(especialidade);
    }

    /**
     *
     * @param id
     * @param especialidade
     */
    @Transactional
    public void updateStatus(Long id, Especialidade especialidade){
        if (id == especialidade.getId()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dataNow = LocalDateTime.parse(dtf.format(LocalDateTime.now()));
            this.especialidadeRepository.setUpdateExcluido(especialidade.getId(), dataNow);
        }
        else {
            throw new RuntimeException();
        }
    }
}
