package br.com.uniamerica.api.service;

import br.com.uniamerica.api.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoricoService {
    @Autowired
    private HistoricoRepository historicoRepository;
}
