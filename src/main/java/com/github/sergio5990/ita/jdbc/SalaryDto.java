package com.github.sergio5990.ita.jdbc;

public class SalaryDto {
    private final Long id;
    private final int money;
    private final String dept;

    public SalaryDto(Long id, int money, String dept) {
        this.id = id;
        this.money = money;
        this.dept = dept;
    }

    public Long getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public String getDept() {
        return dept;
    }

    @Override
    public String toString() {
        return "SalaryDto{" +
                "id=" + id +
                ", money=" + money +
                ", dept='" + dept + '\'' +
                '}';
    }
}
