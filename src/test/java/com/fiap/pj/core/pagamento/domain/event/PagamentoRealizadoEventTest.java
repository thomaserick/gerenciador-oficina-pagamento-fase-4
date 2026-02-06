package com.fiap.pj.core.pagamento.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PagamentoRealizadoEventTest {

    @Test
    @DisplayName("Deve criar PagamentoRealizadoEvent com ordemServicoId correto")
    void deveCriarPagamentoRealizadoEvent() {
        String ordemServicoId = "os-123";

        PagamentoRealizadoEvent event = new PagamentoRealizadoEvent(ordemServicoId);

        assertThat(event.ordemServicoId()).isEqualTo(ordemServicoId);
    }

}
