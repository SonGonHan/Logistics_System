-- SQL-функция для динамического расчёта зарплаты
CREATE OR REPLACE FUNCTION calculate_employee_salary(emp_id BIGINT)
    RETURNS DECIMAL(12,2) AS $$
DECLARE
    years_worked INTEGER;
    pos_record RECORD;
    final_salary DECIMAL(12,2);
BEGIN
    SELECT
        EXTRACT(YEAR FROM AGE(CURRENT_DATE, e.hire_date))::INTEGER AS years,
        p.base_salary,
        p.experience_bonus_percent,
        p.max_experience_years
    INTO pos_record
    FROM hr_service.employees e
             JOIN hr_service.positions p ON e.position_id = p.position_id
    WHERE e.employee_id = emp_id;

    years_worked := LEAST(pos_record.years, pos_record.max_experience_years);
    final_salary := pos_record.base_salary *
                    (1 + (years_worked * pos_record.experience_bonus_percent / 100));

    RETURN final_salary;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION calculate_employee_salary(BIGINT) IS 'Динамический расчёт зарплаты на основе опыта';

-- Cleanup function for Spring Modulith
CREATE OR REPLACE FUNCTION shared_data.cleanup_completed_events()
    RETURNS void AS '
    BEGIN
        DELETE FROM shared_data.event_publication
        WHERE completion_date IS NOT NULL
          AND completion_date < NOW() - INTERVAL ''7 days'';
    END;
' LANGUAGE plpgsql;