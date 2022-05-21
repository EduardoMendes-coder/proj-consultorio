package br.com.uniamerica.api.service;

import br.com.uniamerica.api.entity.Agenda;
import br.com.uniamerica.api.entity.StatusAgenda;
import br.com.uniamerica.api.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
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
        validateUpdate(agenda);
        saveTransactional(agenda);
    }

    public void insert(Agenda agenda){
        validateInsert(agenda);
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


    //----------------------------------------MÉTODOS DE VALIDAÇÕES--------------------------------------------------

    private boolean checkPatientIsNull(Agenda agenda){
        if(agenda.getPaciente() == null || agenda.getPaciente().getId() == null){
            return false;
        }else{
            return true;
        }
    }

    private boolean checkDoctorIsNull(Agenda agenda){
        if(agenda.getMedico() == null || agenda.getMedico().getId() == null){
            return false;
        }else{
            return true;
        }
    }

    private boolean checkDateFuture(LocalDateTime localDateTime){
        if(localDateTime.compareTo(LocalDateTime.now()) >= 0){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkOpeningHours(LocalDateTime localDateTime){
        if(localDateTime.getHour() > 8 && localDateTime.getHour() < 12 && localDateTime.getHour() > 14
                && localDateTime.getHour() < 18){
            return true;
        }else {
            return false;
        }
    }

    private boolean endDateGreaterStart(Agenda agenda){
        if(agenda.getDataAte().compareTo(agenda.getDataDe()) > 0){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkBusinessDay(LocalDateTime data){
        if(data.getDayOfWeek() == DayOfWeek.SATURDAY ||
            data.getDayOfWeek() == DayOfWeek.SUNDAY){
            return false;
        }else {
            return true;
        }
    }

    private boolean checkOverlaps(Agenda agenda){
        if(agenda.getEncaixe() == null) {
            if (this.agendaRepository.findOverlaps(agenda.getDataDe(), agenda.getDataAte(), agenda.getMedico().getId())
                    .size() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSameTimeDoctor(Agenda agenda){
        if(this.agendaRepository.sameTimeAndDoctor(agenda.getDataDe(), agenda.getDataAte(),
                agenda.getMedico().getId()).size() > 0){
            return false;
        }else {
            return true;
        }
    }

    private boolean checkSameTimePatient(Agenda agenda){
        if(this.agendaRepository.sameTimeAndPatient(agenda.getDataDe(), agenda.getDataAte(),
                agenda.getPaciente().getId()).size() > 0){
            return false;
        }else {
            return true;
        }
    }

    private boolean validateDateStatus(Agenda agenda){
        if(agenda.getStatus().equals(StatusAgenda.pendente) && agenda.getDataDe().compareTo(LocalDateTime.now()) < 0){
            return false;
        }
        else if(agenda.getStatus().equals(StatusAgenda.compareceu)
                && agenda.getDataDe().compareTo(LocalDateTime.now()) > 0){
            return false;
        }
        else if(agenda.getStatus().equals(StatusAgenda.nao_compareceu)
                && agenda.getDataDe().compareTo(LocalDateTime.now()) > 0){
            return false;
        }
        return true;
    }

    public void updateStatusRejected(Agenda agenda){
        if(agenda.getStatus().equals(StatusAgenda.pendente) && agenda.getSecretaria() != null){
            agenda.setStatus(StatusAgenda.rejeitado);
        }else {
            throw new RuntimeException("Para o agendamento ser REJEITADO o status precisa ser pendente");
        }
    }

    public void updateStatusApproved(Agenda agenda){
        if(agenda.getStatus().equals(StatusAgenda.pendente) && agenda.getSecretaria() != null){
            agenda.setStatus(StatusAgenda.aprovado);
        }else{
            throw new RuntimeException("Para o agendamento ser APROVADO o status precisa ser pendente");
        }
    }

    public void updateStatusCanceled(Agenda agenda){
        if(agenda.getStatus().equals(StatusAgenda.pendente) || agenda.getStatus().equals(StatusAgenda.aprovado)){
            if(agenda.getSecretaria() != null || agenda.getPaciente() != null){
                agenda.setStatus(StatusAgenda.cancelado);
            }else{
                throw new RuntimeException("Deve haver paciente ou secretaria para o agendamento ser cancelado");
            }
        }else{
            throw new RuntimeException("Para o agendamento ser CANCELADO o status precisa ser pendete ou aprovado");
        }
    }

    public void updateStatusAttend(Agenda agenda){
        if(agenda.getStatus().equals(StatusAgenda.aprovado) && agenda.getSecretaria() != null){
            Assert.isTrue(validateDateStatus(agenda));
            agenda.setStatus(StatusAgenda.compareceu);
        }else{
            throw new RuntimeException("Para o agendamento ser = COMPARECEU o status precisa ser aprovado");
        }
    }

    public void updateStatusNotAttend(Agenda agenda){
        if(agenda.getStatus().equals(StatusAgenda.aprovado) && agenda.getSecretaria() != null){
            Assert.isTrue(validateDateStatus(agenda));
            agenda.setStatus(StatusAgenda.nao_compareceu);
        }else{
            throw new RuntimeException("Para o agendamento ser = NAO_COMPARECEU o status precisa ser aprovado")
        }
    }

    private void essentialValidation(Agenda agenda){
        Assert.isTrue(checkPatientIsNull(agenda));
        Assert.isTrue(checkDoctorIsNull(agenda));
        Assert.isTrue(checkDateFuture(agenda.getDataDe()));
        Assert.isTrue(checkDateFuture(agenda.getDataAte()));
        Assert.isTrue(checkOpeningHours(agenda.getDataDe()));
        Assert.isTrue(checkOpeningHours(agenda.getDataAte()));
        Assert.isTrue(endDateGreaterStart(agenda));
        Assert.isTrue(checkBusinessDay(agenda.getDataDe()));
        Assert.isTrue(checkBusinessDay(agenda.getDataAte()));
        Assert.isTrue(checkOverlaps(agenda));
        Assert.isTrue(checkSameTimePatient(agenda));
        Assert.isTrue(checkSameTimeDoctor(agenda));
        Assert.isTrue(validateDateStatus(agenda));
    }

    public void validateUpdate(Agenda agenda){
        essentialValidation(agenda);
    }

    public void validateInsert(Agenda agenda){
        if(agenda.getSecretaria() == null || agenda.getSecretaria().getId() == null){
            essentialValidation(agenda);
            agenda.setStatus(StatusAgenda.pendente);
        }else{
            essentialValidation(agenda);
            agenda.setStatus(StatusAgenda.aprovado);
        }
    }
}