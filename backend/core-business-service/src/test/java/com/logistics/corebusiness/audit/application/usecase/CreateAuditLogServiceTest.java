package com.logistics.corebusiness.audit.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.audit.application.port.out.AuditLogRepository;
import com.logistics.corebusiness.audit.domain.AuditLog;
import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditActionTypeService auditActionTypeService;

    @InjectMocks
    private CreateAuditLogService createAuditLogService;

    private AuditActionType draftCreateType;

    @BeforeEach
    void setUp() {
        draftCreateType = AuditActionType.builder()
                .id((short) 17)
                .actionName("DRAFT_CREATE")
                .category("Draft")
                .description("Создание черновика")
                .build();

        when(auditActionTypeService.getActionTypeActionName("DRAFT_CREATE"))
                .thenReturn(Optional.of(draftCreateType));
        when(auditActionTypeService.getActionTypeActionName("DRAFT_UPDATE"))
                .thenReturn(Optional.of(createActionType(18, "DRAFT_UPDATE")));
        when(auditActionTypeService.getActionTypeActionName("DRAFT_CANCEL"))
                .thenReturn(Optional.of(createActionType(19, "DRAFT_CANCEL")));
        when(auditActionTypeService.getActionTypeActionName("WAYBILL_FINALIZE"))
                .thenReturn(Optional.of(createActionType(20, "WAYBILL_FINALIZE")));
        when(auditActionTypeService.getActionTypeActionName("WAYBILL_STATUS_CHANGE"))
                .thenReturn(Optional.of(createActionType(21, "WAYBILL_STATUS_CHANGE")));
        when(auditActionTypeService.getActionTypeActionName("WAYBILL_CANCEL"))
                .thenReturn(Optional.of(createActionType(24, "WAYBILL_CANCEL")));
        when(auditActionTypeService.getActionTypeActionName("WAYBILL_SERVICE_ADD"))
                .thenReturn(Optional.of(createActionType(26, "WAYBILL_SERVICE_ADD")));
        when(auditActionTypeService.getActionTypeActionName("PVZ_ACCEPTANCE_START"))
                .thenReturn(Optional.of(createActionType(27, "PVZ_ACCEPTANCE_START")));
        when(auditActionTypeService.getActionTypeActionName("PVZ_WEIGHT_VERIFY"))
                .thenReturn(Optional.of(createActionType(28, "PVZ_WEIGHT_VERIFY")));
        when(auditActionTypeService.getActionTypeActionName("PVZ_PHOTO_UPLOAD"))
                .thenReturn(Optional.of(createActionType(29, "PVZ_PHOTO_UPLOAD")));
        when(auditActionTypeService.getActionTypeActionName("RATING_SUBMIT"))
                .thenReturn(Optional.of(createActionType(77, "RATING_SUBMIT")));
        when(auditActionTypeService.getActionTypeActionName("WAYBILL_CREATE"))
                .thenReturn(Optional.of(createActionType(80, "WAYBILL_CREATE")));

        createAuditLogService.initActionTypeCache();
    }

    @Test
    void shouldCreateAuditLogWithValidCommand() {
        var command = new CreateAuditLogCommand(
                1L,
                "DRAFT_CREATE",
                "+79001234567",
                "192.168.1.1",
                Map.of("barcode", "DRF-260209-123456", "pricingRuleId", 5L),
                "waybill_drafts",
                10L
        );

        createAuditLogService.create(command);

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        var savedLog = captor.getValue();
        assertThat(savedLog.getUserId()).isEqualTo(1L);
        assertThat(savedLog.getActionType()).isEqualTo(draftCreateType);
        assertThat(savedLog.getActorIdentifier()).isEqualTo("+79001234567");
        assertThat(savedLog.getIpAddress()).isNotNull();
        assertThat(savedLog.getNewValues()).containsEntry("barcode", "DRF-260209-123456");
        assertThat(savedLog.getTableName()).isEqualTo("waybill_drafts");
        assertThat(savedLog.getRecordId()).isEqualTo(10L);
    }

    @Test
    void shouldHandleNullUserId() {
        var command = new CreateAuditLogCommand(
                null,
                "DRAFT_CREATE",
                "+79001234567",
                "192.168.1.1",
                Map.of("barcode", "DRF-260209-123456"),
                "waybill_drafts",
                10L
        );

        createAuditLogService.create(command);

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        var savedLog = captor.getValue();
        assertThat(savedLog.getUserId()).isNull();
        assertThat(savedLog.getActorIdentifier()).isEqualTo("+79001234567");
    }

    @Test
    void shouldNotThrowWhenAuditFails() {
        doThrow(new RuntimeException("Database error")).when(auditLogRepository).save(any());

        var command = new CreateAuditLogCommand(
                1L,
                "DRAFT_CREATE",
                "+79001234567",
                "192.168.1.1",
                Map.of("barcode", "DRF-260209-123456"),
                "waybill_drafts",
                10L
        );

        createAuditLogService.create(command);

        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldHandleNullIpAddress() {
        var command = new CreateAuditLogCommand(
                1L,
                "DRAFT_CREATE",
                "+79001234567",
                null,
                Map.of("barcode", "DRF-260209-123456"),
                "waybill_drafts",
                10L
        );

        createAuditLogService.create(command);

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        var savedLog = captor.getValue();
        assertThat(savedLog.getIpAddress()).isNull();
    }

    @Test
    void shouldReturnEarlyWhenActionTypeNotInCache() {
        var command = new CreateAuditLogCommand(
                1L,
                "UNKNOWN_ACTION",
                "+79001234567",
                "192.168.1.1",
                Map.of("barcode", "DRF-260209-123456"),
                "waybill_drafts",
                10L
        );

        createAuditLogService.create(command);

        verify(auditLogRepository, never()).save(any());
    }

    private AuditActionType createActionType(int id, String actionName) {
        return AuditActionType.builder()
                .id((short) id)
                .actionName(actionName)
                .category("TestCategory")
                .description("Test description")
                .build();
    }
}
