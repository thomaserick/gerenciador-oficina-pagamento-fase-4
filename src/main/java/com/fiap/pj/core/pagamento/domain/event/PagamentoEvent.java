package com.fiap.pj.core.pagamento.domain.event;

import java.util.UUID;

public record PagamentoEvent(UUID ordemServicoId) {
}
