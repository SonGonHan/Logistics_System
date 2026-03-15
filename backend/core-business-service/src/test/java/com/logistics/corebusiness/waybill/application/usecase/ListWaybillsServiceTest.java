package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.application.port.in.command.ListWaybillsCommand;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListWaybillsService - тестирование списка накладных")
class ListWaybillsServiceTest {

    @Mock
    private WaybillRepository waybillRepository;

    @InjectMocks
    private ListWaybillsService service;

    @Test
    @DisplayName("Должен вернуть накладные отправителя")
    void shouldReturnWaybillsForSender() {
        when(waybillRepository.findBySenderUserId(1L)).thenReturn(List.of(
                createWaybill(1L, "WB-1"),
                createWaybill(2L, "WB-2")
        ));

        var response = service.list(ListWaybillsCommand.builder().senderUserId(1L).build());

        assertThat(response).hasSize(2);
        assertThat(response).extracting("waybillNumber").containsExactly("WB-1", "WB-2");
    }

    @Test
    @DisplayName("Должен вернуть пустой список когда накладных нет")
    void shouldReturnEmptyListWhenNoWaybills() {
        when(waybillRepository.findBySenderUserId(99L)).thenReturn(List.of());

        var response = service.list(ListWaybillsCommand.builder().senderUserId(99L).build());

        assertThat(response).isEmpty();
    }

    private Waybill createWaybill(Long id, String number) {
        return Waybill.builder()
                .id(id)
                .waybillNumber(number)
                .waybillCreatorId(1L)
                .senderUserId(1L)
                .recipientUserId(2L)
                .recipientAddress("Адрес")
                .pricingRuleId(1L)
                .finalPrice(new BigDecimal("100.00"))
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();
    }
}
