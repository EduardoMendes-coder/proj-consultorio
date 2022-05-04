package br.com.uniamerica.api.service;

import br.com.uniamerica.api.entity.Convenio;
import br.com.uniamerica.api.entity.Especialidade;
import br.com.uniamerica.api.repository.ConvenioRepository;
import br.com.uniamerica.api.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Optional;

public class ConvenioService {
    @Autowired
    private ConvenioRepository convenioRepository;

    /**
     *
     * @param id
     * @return
     */
    public Optional<Convenio> findById(Long id){
        return this.convenioRepository.findById(id);
    }

    /**
     *
     * @param pageable
     * @return
     */
    public Page<Convenio> listAll(Pageable pageable){
        return this.convenioRepository.findAll(pageable);
    }

    /**
     *
     * @param id
     * @param convenio
     */
    @Transactional
    public void update(Long id, Convenio convenio){
        if (id == convenio.getId()) {
            this.convenioRepository.save(convenio);
        }
        else {
            throw new RuntimeException();
        }
    }

    /**
     *
     * @param convenio
     */
    @Transactional
    public void insert(Convenio convenio){
        this.convenioRepository.save(convenio);
    }

    /**
     *
     * @param id
     * @param convenio
     */
    @Transactional
    public void updateStatus(Long id, Convenio convenio){
        if (id == convenio.getId()) {
            this.convenioRepository.setUpdateExcluido(convenio.getId());
        }
        else {
            throw new RuntimeException();
        }
    }
}
