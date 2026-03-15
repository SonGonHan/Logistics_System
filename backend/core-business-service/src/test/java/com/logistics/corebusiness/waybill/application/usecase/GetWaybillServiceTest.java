package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.application.exception.WaybillNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.command.GetWaybillCommand;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetWaybillService - тестирование получения накладной")
class GetWaybillServiceTest {

    @Mock
    private WaybillRepository waybillRepository;

    @InjectMocks
    private GetWaybillService service;

    @Test
    @DisplayName("Должен успешно получить накладную по ID")
    void shouldGetWaybillByIdSuccessfully() {
        when(waybillRepository.findById(1L)).thenReturn(Optional.of(createWaybill()));

        var response = service.get(GetWaybillCommand.builder().id(1L).build());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.waybillNumber()).isEqualTo("WB-260315-123456");
    }

    @Test
    @DisplayName("Должен успешно получить накладную по номеру")
    void shouldGetWaybillByNumberSuccessfully() {
        when(waybillRepository.findByWaybillNumber("WB-260315-123456")).thenReturn(Optional.of(createWaybill()));

        var response = service.get(GetWaybillCommand.builder().waybillNumber("WB-260315-123456").build());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.waybillNumber()).isEqualTo("WB-260315-123456");
    }

    @Test
    @DisplayName("Должен выбросить исключение когда накладная не найдена по ID")
    void shouldThrowWhenWaybillNotFoundById() {
        when(waybillRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(GetWaybillCommand.builder().id(55L).build()))
                .isInstanceOf(WaybillNotFoundException.class)
                .hasMessage("Waybill not found with id: 55");
    }

    @Test
    @DisplayName("Должен выбросить исключение когда накладная не найдена по номеру")
    void shouldThrowWhenWaybillNotFoundByNumber() {
        when(waybillRepository.findByWaybillNumber("WB-NOT-FOUND")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(GetWaybillCommand.builder().waybillNumber("WB-NOT-FOUND").build()))
                .isInstanceOf(WaybillNotFoundException.class)
                .hasMessage("Waybill not found with number: WB-NOT-FOUND");
    }

    private Waybill createWaybill() {
        return Waybill.builder()
                .id(1L)
                .waybillNumber("WB-260315-123456")
                .waybillCreatorId(1L)
                .senderUserId(1L)
                .recipientUserId(2L)
                .recipientAddress("Москва")
                .pricingRuleId(3L)
                .finalPrice(new BigDecimal("500.00"))
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();
    }
}
