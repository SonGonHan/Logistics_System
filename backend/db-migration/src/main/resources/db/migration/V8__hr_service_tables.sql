-- ============================================================================
-- 8. СОЗДАНИЕ ТАБЛИЦ hr_service
-- ============================================================================

CREATE TABLE hr_service.positions (
                                      position_id BIGSERIAL PRIMARY KEY,
                                      position_name VARCHAR(100) NOT NULL,
                                      role_name VARCHAR(50) NOT NULL,
                                      region VARCHAR(100) NOT NULL,
                                      base_salary DECIMAL(12,2) NOT NULL,
                                      experience_bonus_percent DECIMAL(5,2) NOT NULL DEFAULT 5.00,
                                      max_experience_years INTEGER DEFAULT 10,
                                      UNIQUE(position_name, region)
);

COMMENT ON COLUMN hr_service.positions.experience_bonus_percent IS 'Процент надбавки за каждый год опыта';
COMMENT ON COLUMN hr_service.positions.max_experience_years IS 'Максимум лет для расчёта надбавки';

CREATE TABLE hr_service.employees (
                                      employee_id BIGSERIAL PRIMARY KEY,
                                      user_id BIGINT REFERENCES user_management.users(user_id) ON DELETE SET NULL,
                                      employee_number VARCHAR(20) UNIQUE NOT NULL,
                                      position_id BIGINT REFERENCES hr_service.positions(position_id) ON DELETE SET NULL,
                                      employment_status VARCHAR(30) DEFAULT 'ACTIVE',
                                      hire_date DATE NOT NULL,
                                      termination_date DATE
);

COMMENT ON TABLE hr_service.employees IS 'Зарплата рассчитывается динамически через функцию';


CREATE TABLE hr_service.employee_payments (
                                              payment_id BIGSERIAL PRIMARY KEY,
                                              employee_id BIGINT NOT NULL REFERENCES hr_service.employees(employee_id) ON DELETE CASCADE,
                                              payment_type VARCHAR(30) NOT NULL,
                                              period_start DATE,
                                              period_end DATE,
                                              amount DECIMAL(12,2) NOT NULL,
                                              description TEXT,
                                              payment_status VARCHAR(20) DEFAULT 'PENDING',
                                              paid_at TIMESTAMP,
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

