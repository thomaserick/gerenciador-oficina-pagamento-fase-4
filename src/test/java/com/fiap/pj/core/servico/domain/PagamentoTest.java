package com.fiap.pj.core.servico.domain;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.servico.util.factory.PagamentoTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PagamentoTest {


    @Test
    @DisplayName("Deve criar com sucesso uma instância de servico.")
    void deveCriarServico() {
//        var service = PagamentoTestFactory.umServico();
//
//
//        assertEquals(PagamentoTestFactory.ID, service.getId());

    }

    @Nested
    class FalhaNaCriacao {

        @Test
        void deveFalharComIdInvalido() {
            assertThrows(NullPointerException.class,
                    () -> new Pagamento(null,
                            null,
                            null,
                            null, true));
        }

        @Test
        void deveFalharComDescricaoNula() {
            assertThrows(NullPointerException.class,
                    () -> new Pagamento(PagamentoTestFactory.ID,
                            null,
                            null,
                            null, true));
        }


    }
}
