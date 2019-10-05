package com.github.sergio5990.ita.jdbc;

import java.util.List;

public interface SalaryDao {
    SalaryDto save(SalaryDto salary);

    SalaryDto saveTransaction(SalaryDto salary);

    List<SalaryDto> save(List<SalaryDto> salaries);

    boolean delete(long id);

    SalaryDto get(long id);

    boolean update(SalaryDto salary);

    List<SalaryDto> findByDept(String dept);

}
