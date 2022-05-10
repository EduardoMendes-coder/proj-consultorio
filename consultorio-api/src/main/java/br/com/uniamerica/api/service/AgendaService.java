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
        dtf.format(LocalDateTime.now());
        if (id == agenda.getId()) {
            this.agendaRepository.setUpdateExcluido(agenda.getId(), dtf);
        }
        else {
            throw new RuntimeException();
        }
    }

    public void validarFormulario(Agenda agenda){
        if (agenda.getPaciente().getId() == null){
            throw new RuntimeException("Paciente não informado");
        }
        if (agenda.getMedico().getId() == null){
            throw new RuntimeException("Médico não informado");
        }
        if (agenda.getStatus().equals(StatusAgenda.pendente) && agenda.getData().compareTo(LocalDateTime.now()) <= 0){
            throw new RuntimeException("Data do agendamento menor do que data atual");
        }
        if (agenda.getStatus().equals(StatusAgenda.compareceu) && agenda.getData().compareTo(LocalDateTime.now()) > 0
        || agenda.getStatus().equals(StatusAgenda.nao_compareceu) && agenda.getData().compareTo(LocalDateTime.now()) > 0){
            throw new RuntimeException("Erro: StatusAgenda = compareceu e data de atendimento maior que data atual");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        LocalDateTime horaAgendamento = LocalDateTime.parse(sdf.format(agenda.getData()));
        LocalDateTime horaAtendMax = LocalDateTime.parse(sdf.format("18:00"));
        LocalDateTime horaAlmoco = LocalDateTime.parse(sdf.format("12:00"));
        LocalDateTime horaVoltaDoAlmoco = LocalDateTime.parse(sdf.format("14:00"));
        if(horaAgendamento.compareTo(horaAtendMax) >= 0 || horaAgendamento.compareTo(horaAlmoco) >= 0
                && horaAgendamento.compareTo(horaVoltaDoAlmoco) < 0){
            throw new RuntimeException("Horário do agendamento está fora do horário de atendimento");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dataAgendaFormDia = LocalDateTime.parse(dtf.format(agenda.getData()));
        LocalDateTime dataAgendaFormHorario = LocalDateTime.parse(sdf.format(agenda.getData()));
        if(this.agendaRepository.listIdPacienteAgenda(dataAgendaFormDia,
                agenda.getMedico().getId()).contains(agenda.getId())){
            throw new RuntimeException("Paciente já possui horário marcado no dia de hoje");
        }
        if(this.agendaRepository.listIdPacienteAgenda(dataAgendaFormHorario, agenda.getMedico().getId()) == null){
            throw new RuntimeException("Já existe um paciente agendado nesse horário");
        }
        if(agenda.getSecretaria() == null){
            agenda.setStatus(StatusAgenda.pendente);
        }
        if(agenda.getSecretaria() != null){
            agenda.setStatus(StatusAgenda.aprovado);
        }
    }
}
