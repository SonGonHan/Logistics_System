package com.logistics.shared.company_facilities;

import com.logistics.shared.company_facilities.domain.Facility;
import com.logistics.shared.company_facilities.domain.FacilityType;
import com.logistics.shared.company_facilities.persistence.FacilityJpaRepository;
import com.logistics.shared.company_facilities.persistence.FacilityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Бизнес-сервис для работы с объектами компании (ПВЗ, склады, офисы).
 *
 * <h2>Ответственность</h2>
 * Предоставляет функциональность для:
 * - Получения всех объектов
 * - Фильтрации по типу объекта ({@link FacilityType})
 * - Получения только активных объектов по типу
 * - Поиска объекта по ID
 *
 * <h2>Понятие «активный объект»</h2>
 * Объект считается активным, если поле {@code closedDate} равно {@code null}
 * либо дата закрытия ещё не наступила (strictly after today).
 *
 * @see FacilityJpaRepository для работы с БД
 * @see FacilityMapper для преобразования Entity ↔ Domain
 * @see Facility доменная модель
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityJpaRepository repository;
    private final FacilityMapper mapper;

    /**
     * Возвращает все объекты компании без фильтрации.
     *
     * @return список всех объектов (включая закрытые)
     */
    public List<Facility> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Возвращает объекты компании указанного типа (включая закрытые).
     *
     * @param type тип объекта: {@code PVZ}, {@code WAREHOUSE} или {@code OFFICE}
     * @return список объектов заданного типа
     */
    public List<Facility> getByType(FacilityType type) {
        log.debug("Запрос объектов по типу: {}", type);
        return repository.findAllByType(type).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Ищет объект по идентификатору.
     *
     * @param id идентификатор объекта
     * @return {@link Optional} с объектом, или {@code Optional.empty()} если не найден
     */
    public Optional<Facility> getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    /**
     * Возвращает <b>активные</b> объекты компании указанного типа.
     * Активным считается объект, у которого {@code closedDate} равен {@code null}
     * или ещё не наступил.
     *
     * @param type тип объекта: {@code PVZ}, {@code WAREHOUSE} или {@code OFFICE}
     * @return список активных объектов заданного типа
     */
    public List<Facility> getActiveByType(FacilityType type) {
        return repository.findAllByType(type).stream()
                .map(mapper::toDomain)
                .filter(r -> isActive(r))
                .toList();
    }

    /**
     * Проверяет, является ли объект активным на текущую дату.
     *
     * @param facility объект компании
     * @return {@code true} если объект ещё не закрыт
     */
    public boolean isActive(Facility facility) {
        return facility.getClosedDate() == null
                || facility.getClosedDate().isAfter(LocalDate.now());
    }
}