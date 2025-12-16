package com.logistics.shared.audit_action;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionJpaRepository;
import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditActionTypeService: юнит-тесты")
class AuditActionTypeServiceTest {

    @Mock
    private AuditActionJpaRepository repo;
    @Mock
    private AuditActionTypeMapper mapper;
    @InjectMocks
    private AuditActionTypeService service;

    private AuditActionTypeEntity testEntity;
    private AuditActionType testDomain;

    @BeforeEach
    void setUp() {
        testEntity = AuditActionTypeEntity.builder().id((short) 1).actionName("USER_LOGIN").category("AUTHENTICATION").description("User logged in").build();
        testDomain = AuditActionType.builder().id((short) 1).actionName("USER_LOGIN").category("AUTHENTICATION").description("User logged in").build();
    }

    @Test
    @DisplayName("Должен вернуть ActionType по ID")
    void shouldGetActionTypeById() {
        when(repo.findById(anyInt())).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(any(AuditActionTypeEntity.class))).thenReturn(testDomain);

        Optional<AuditActionType> result = service.getActionTypeById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo((short) 1);
        verify(repo, times(1)).findById(1);
    }
}

