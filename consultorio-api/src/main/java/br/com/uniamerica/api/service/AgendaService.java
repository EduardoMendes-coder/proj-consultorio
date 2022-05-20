package br.com.uniamerica.api.service;

import br.com.uniamerica.api.entity.Agenda;
import br.com.uniamerica.api.entity.StatusAgenda;
import br.com.uniamerica.api.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AgendaService {
    @Autowired
    private AgendaRepository agendaRepository;

    /**
     *
     * @param id
     * @return
     */
    public Optional<Agenda> findById(Long id){
        return this.agendaRepository.findById(id);
    }

    /**
     *
     * @param pageable
     * @return
     */
    public Page<Agenda> findAll(Pageable pageable){
        return this.agendaRepository.findAll(pageable);
    }

    /**
     *
     * @param id
     * @param agenda
     */
    public void update(Long id, Agenda agenda){
        validarFormulario(agenda);
        saveTransactional(agenda);
    }

    public void insert(Agenda agenda){
        validarFormulario(agenda);
        saveTransactional(agenda);
    }

    /**
     *
     * @param agenda
     */
    @Transactional
    public void saveTransactional(Agenda agenda){
        this.agendaRepository.save(agenda);
    }

    /**
     *
     * @param id
     * @param agenda
     */
    @Transactional
    public void updateStatus(Long id, Agenda agenda){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateUpdateStatus = LocalDateTime.parse(dtf.format(LocalDateTime.now()));
        if (id == agenda.getId()) {
            this.agendaRepository.setUpdateExcluido(agenda.getId(), dateUpdateStatus);
        }
        else {
            throw new RuntimeException();
        }
    }

    public void validarFormulario(Agenda agenda){
        if (agenda.getPaciente() == null || agenda.getPaciente().getId() == null){
            throw new RuntimeException("Paciente não informado");
        }

        if(agenda.getMedico() == null || agenda.getMedico().getId() == null){
            throw new RuntimeException("Médico não informado");
        }

        if(agenda.getDataDe().compareTo(LocalDateTime.now()) < 0){
            throw new RuntimeException("Data de agendamento menor que a data atual");
        }

        if(agenda.getStatus().equals(StatusAgenda.pendente)){
            if(agenda.getDataDe().compareTo(LocalDateTime.now()) < 0 ||
                    agenda.getDataAte().compareTo(LocalDateTime.now()) < 0){
                throw new RuntimeException(" Status pendente e data do agendamento menor do que data atual");
            }
        }

        if(agenda.getStatus().equals(StatusAgenda.compareceu) || agenda.getStatus().equals(StatusAgenda.nao_compareceu)){
            if(agenda.getDataDe().compareTo(LocalDateTime.now()) > 0 || agenda.getDataAte().compareTo(LocalDateTime.now()) > 0){
                throw new RuntimeException("Erro: StatusAgenda = compareceu ou nao_compareceu e data de atendimento maior" +
                " que data atual");
            }
        }
    }

    //----------------------------------------MÉTODOS DE VALIDAÇÕES--------------------------------------------------

    private boolean CheckDateFuture(LocalDateTime localDateTime){
        if(localDateTime.compareTo(LocalDateTime.now()) > 0){
            return true;
        }else{
            return false;
        }
    }

    private boolean CheckOpeningHours(LocalDateTime localDateTime){
        if(localDateTime.getHour() > 8 && localDateTime.getHour() < 12 && localDateTime.getHour() > 14
                && localDateTime.getHour() < 18){
            return true;
        }else {
            return false;
        }
    }

    private boolean DateAteMenorQueDateAte(LocalDateTime localDateTime1, LocalDateTime localDateTime2){
        if(localDateTime2.compareTo(localDateTime1) > 0){
            return true;
        }else{
            return false;
        }
    }

    private boolean CheckBusinessDay(Long id){
        if(this.agendaRepository.checkBusinessDay(id).contains(0)
                || this.agendaRepository.checkBusinessDay(id).contains(6)){
            return false;
        }else{
            return true;
        }
    }

    private boolean CheckOverlaps(LocalDateTime dataDe, LocalDateTime dataAte, Long idMedico){
        if(this.agendaRepository.findOverlaps(dataDe, dataAte, idMedico).size() > 0){
            return true;
        }else{
            return false;
        }
    }

    private boolean CheckSameTimeDoctor(LocalDateTime d1, LocalDateTime d2, Long idMedico){
        if(this.agendaRepository.sameTimeAndDoctor(d1, d2, idMedico).size() > 0){
            return true;
        }else {
            return false;
        }
    }

    private boolean CheckSameTimePatient(LocalDateTime d1, LocalDateTime d2, Long idPaciente){
        if(this.agendaRepository.sameTimeAndPatient(d1, d2, idPaciente).size() > 0){
            return true;
        }else {
            return false;
        }
    }
}