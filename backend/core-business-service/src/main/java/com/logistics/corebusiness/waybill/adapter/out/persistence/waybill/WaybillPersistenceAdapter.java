package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Адаптер для работы с репозиторием накладных через JPA.
 */
@Component
@RequiredArgsConstructor
public class WaybillPersistenceAdapter implements WaybillRepository {

    private final WaybillJpaRepository jpaRepository;
    private final WaybillPersistenceMapper mapper;

    @Override
    public Waybill save(Waybill waybill) {
        WaybillEntity entity = mapper.toEntity(waybill);
        WaybillEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Waybill waybill) {
        WaybillEntity entity = mapper.toEntity(waybill);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Waybill> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Waybill> findByWaybillNumber(String waybillNumber) {
        return jpaRepository.findByWaybillNumber(waybillNumber).map(mapper::toDomain);
    }

    @Override
    public List<Waybill> findBySenderUserId(Long senderUserId) {
        return jpaRepository.findBySenderUserId(senderUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Waybill> findByRecipientUserId(Long recipientUserId) {
        return jpaRepository.findByRecipientUserId(recipientUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Waybill> findByStatus(WaybillStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Waybill> findByWaybillCreatorId(Long creatorId) {
        return jpaRepository.findByWaybillCreatorId(creatorId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
