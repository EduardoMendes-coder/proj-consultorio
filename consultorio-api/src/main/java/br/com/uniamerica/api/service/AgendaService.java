package br.com.uniamerica.api.service;

import br.com.uniamerica.api.entity.Agenda;
import br.com.uniamerica.api.entity.StatusAgenda;
import br.com.uniamerica.api.repository.AgendaRepository;
import org.apache.tomcat.jni.Local;
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

        if(agenda.getStatus().equals(StatusAgenda.pendente)){
            if(agenda.getDataDe().compareTo(LocalDateTime.now()) < 0 ||
                    agenda.getDataAte().compareTo(LocalDateTime.now()) < 0){
                throw new RuntimeException("Data do agendamento menor do que data atual");
            }
        }

        if(agenda.getStatus().equals(StatusAgenda.compareceu) || agenda.getStatus().equals(StatusAgenda.nao_compareceu)){
            if(agenda.getDataDe().compareTo(LocalDateTime.now()) > 0 || agenda.getDataAte().compareTo(LocalDateTime.now()) > 0){
                throw new RuntimeException("Erro: StatusAgenda = compareceu ou nao_compareceu e data de atendimento maior" +
                " que data atual");
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        LocalDateTime horaDe = LocalDateTime.parse(sdf.format(agenda.getDataDe()));
        LocalDateTime horaAte = LocalDateTime.parse(sdf.format(agenda.getDataAte()));
        LocalDateTime horaInicioAtend = LocalDateTime.parse(sdf.format("08:00"));
        LocalDateTime horaAlmoco = LocalDateTime.parse(sdf.format("12:00"));
        LocalDateTime horaVoltaDoAlmoco = LocalDateTime.parse(sdf.format("14:00"));
        LocalDateTime horaAtendMax = LocalDateTime.parse(sdf.format("18:00"));

        if(horaDe.compareTo(horaInicioAtend) < 0 || horaDe.compareTo(horaAlmoco) >= 0  && horaDe.compareTo(horaVoltaDoAlmoco) < 0
                || horaDe.compareTo(horaAtendMax) >= 0 || horaAte.compareTo(horaAlmoco) > 0 && horaAte.compareTo(horaVoltaDoAlmoco) < 0
                || horaAte.compareTo(horaAtendMax) > 0 && horaAte.compareTo(horaInicioAtend) < 0){
            throw new RuntimeException("Horário do agendamento está fora do horário de atendimento do consultório");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dataAgendaFormDia = LocalDateTime.parse(dtf.format(agenda.getDataDe()));
        if(this.agendaRepository.listPacienteAgendados(dataAgendaFormDia, agenda.getMedico()).contains(agenda.getPaciente())){
            throw new RuntimeException("Paciente já possui horário marcado no dia de hoje com esse médico");
        }

        if(this.agendaRepository.findOverlaps(agenda.getDataDe(), agenda.getDataAte()).size() > 0){
            throw new RuntimeException("Já existe um paciente agendado nesse horário");
        }
    }
}