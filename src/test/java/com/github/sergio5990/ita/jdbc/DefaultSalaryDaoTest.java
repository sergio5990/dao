package com.github.sergio5990.ita.jdbc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSalaryDaoTest {
    final SalaryDao salaryDao = DefaultSalaryDao.getInstance();

    @Test
    void save() {
        final SalaryDto salaryToSave = new SalaryDto(null, 30, "dev");
        final SalaryDto savedSalary = salaryDao.save(salaryToSave);
        assertEquals(salaryToSave.getDept(), savedSalary.getDept());
        assertEquals(salaryToSave.getMoney(), savedSalary.getMoney());
        assertNotNull(savedSalary.getId());
    }

    @Test
    void saveTransaction() {
        final SalaryDto salaryToSave = new SalaryDto(null, 30, "dev");
        final SalaryDto savedSalary = salaryDao.saveTransaction(salaryToSave);
        assertEquals(salaryToSave.getDept(), savedSalary.getDept());
        assertEquals(salaryToSave.getMoney(), savedSalary.getMoney());
        assertNotNull(savedSalary.getId());
    }

    @Test
    void saveList() {
        final SalaryDto salaryToSave1 = new SalaryDto(null, 30, "dev");
        final SalaryDto salaryToSave2 = new SalaryDto(null, 40, "dev");
        final List<SalaryDto> salariesToSave = Arrays.asList(salaryToSave1, salaryToSave2);
        final List<SalaryDto> savedSalaries = salaryDao.save(salariesToSave);
        for (int i = 0; i < salariesToSave.size(); i++) {
            final SalaryDto salaryToSave = salariesToSave.get(i);
            final SalaryDto savedSalary = savedSalaries.get(i);

            assertEquals(salaryToSave.getDept(), savedSalary.getDept());
            assertEquals(salaryToSave.getMoney(), savedSalary.getMoney());
            assertNotNull(savedSalary.getId());
        }
    }

    @Test
    void delete() {
        final SalaryDto salaryToSave = new SalaryDto(null, 30, "dev");
        final SalaryDto savedSalary = salaryDao.save(salaryToSave);
        final Long id = savedSalary.getId();
        final SalaryDto salaryDto = salaryDao.get(id);
        assertNotNull(salaryDto);

        final boolean deleted = salaryDao.delete(id);
        assertTrue(deleted);

        final SalaryDto afterDelete = salaryDao.get(id);
        assertNull(afterDelete);
    }

    @Test
    void get() {
        final SalaryDto salaryToSave = new SalaryDto(null, 30, "dev");
        final SalaryDto savedSalary = salaryDao.save(salaryToSave);
        final Long id = savedSalary.getId();

        final SalaryDto salaryDto = salaryDao.get(id);
        assertNotNull(salaryDto);
        assertEquals(salaryToSave.getDept(), salaryDto.getDept());
        assertEquals(salaryToSave.getMoney(), salaryDto.getMoney());
        assertEquals(id, salaryDto.getId());
    }

    @Test
    void update() {
        final SalaryDto salaryToSave = new SalaryDto(null, 30, "dev");
        final SalaryDto savedSalary = salaryDao.save(salaryToSave);
        final Long id = savedSalary.getId();

        final SalaryDto toUpdate = new SalaryDto(id,
                40,
                savedSalary.getDept());
        final boolean updated = salaryDao.update(toUpdate);
        assertTrue(updated);

        final SalaryDto afterUpdate = salaryDao.get(id);
        assertEquals(toUpdate.getDept(), afterUpdate.getDept());
        assertEquals(toUpdate.getMoney(), afterUpdate.getMoney());

    }

    @Test
    void findByDept() {
        final SalaryDto salaryToSave = new SalaryDto(null, 30, "dev");
        final SalaryDto savedSalary = salaryDao.save(salaryToSave);

        final List<SalaryDto> dev = salaryDao.findByDept("dev");
        assertTrue(dev.size() > 0);
    }
}