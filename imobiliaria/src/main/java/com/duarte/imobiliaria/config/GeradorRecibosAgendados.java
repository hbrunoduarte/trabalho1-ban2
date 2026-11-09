package com.duarte.imobiliaria.config;

import com.duarte.imobiliaria.database.interfaceDAO.ReciboDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GeradorRecibosAgendados {

    private static final Logger log = LoggerFactory.getLogger(GeradorRecibosAgendados.class);

    private final ReciboDAO reciboDAO;

    public GeradorRecibosAgendados(ReciboDAO reciboDAO) {
        this.reciboDAO = reciboDAO;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciar() {
        gerar();
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void diariamente() {
        gerar();
    }

    private void gerar() {
        try {
            int qtd = reciboDAO.gerarRecibosPendentes();
            if (qtd > 0) {
                log.info("{} recibo(s) de aluguel gerado(s)", qtd);
            }
        } catch (RuntimeException e) {
            log.error("Falha ao gerar recibos de aluguel", e);
        }
    }
}
