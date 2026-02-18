/**
 * Слой персистентности контекста объектов компании.
 *
 * <p>Содержит JPA-сущность {@link com.logistics.shared.company_facilities.persistence.FacilityEntity},
 * Spring Data репозиторий {@link com.logistics.shared.company_facilities.persistence.FacilityJpaRepository}
 * и маппер {@link com.logistics.shared.company_facilities.persistence.FacilityMapper}.
 *
 * <p>Ни один класс из этого пакета не должен импортироваться напрямую
 * за пределами {@code company_facilities} — используй
 * {@link com.logistics.shared.company_facilities.FacilityService}.
 */
package com.logistics.shared.company_facilities.persistence;