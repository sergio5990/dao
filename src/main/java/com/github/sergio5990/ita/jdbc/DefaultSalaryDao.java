package com.github.sergio5990.ita.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class DefaultSalaryDao implements SalaryDao {
    private static final Logger log = LoggerFactory.getLogger(DefaultSalaryDao.class);
    private final String url;
    private final String user;
    private final String pass;

    private DefaultSalaryDao() {
        ResourceBundle resource = ResourceBundle.getBundle("db");
        url = resource.getString("url");
        user = resource.getString("user");
        pass = resource.getString("password");
    }

    private static class SingletonHolder {
        static final SalaryDao HOLDER_INSTANCE = new DefaultSalaryDao();
    }

    public static SalaryDao getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private Connection getConnection() throws SQLException {
//        final Connection connection = DriverManager.getConnection(url, user, pass);
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//        return connection;

        return DataSource.getInstance().getConnection();
    }

    @Override
    public SalaryDto save(SalaryDto salary) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into salary(dept, money) values (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, salary.getDept());
            statement.setInt(2, salary.getMoney());
            statement.executeUpdate();
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            final long id = generatedKeys.getLong(1);
            final SalaryDto salaryDto = new SalaryDto(id, salary.getMoney(), salary.getDept());
            log.info("salary saved: {}", salaryDto);
            return salaryDto;
        } catch (SQLException e) {
            log.error("fail to save salary:{}", salary, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryDto saveTransaction(SalaryDto salary) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("insert into salary(dept, money) values (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, salary.getDept());
                statement.setInt(2, salary.getMoney());
                statement.executeUpdate();
                final ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.next();
                final long id = generatedKeys.getLong(1);
                final SalaryDto salaryDto = new SalaryDto(id, salary.getMoney(), salary.getDept());
                log.trace("salary saved: {}", salaryDto);

                log.debug("salary saved: {}", salaryDto);

                log.info("salary saved: {} param2:{} param3: {}", salaryDto, 1, 2);
                log.warn("salary saved: {}", salaryDto);
                log.error("salary saved: {}", salaryDto);

                connection.commit();
                return salaryDto;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                log.error("salary saved: {}", salary, e );
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            log.error("fail to save salary:{}", salary, e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("fail close connection", e);
                }
            }
        }
    }

    @Override
    public List<SalaryDto> save(List<SalaryDto> salaries) {
        Connection connection = null;
        String var = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("insert into salary(dept, money) values (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                for (SalaryDto salary : salaries) {
                    statement.setString(1, salary.getDept());
                    statement.setInt(2, salary.getMoney());
                    statement.addBatch();
                }
                statement.executeBatch();
                final ResultSet generatedKeys = statement.getGeneratedKeys();
                final ArrayList<SalaryDto> result = new ArrayList<>();
                for (SalaryDto salary : salaries) {
                    generatedKeys.next();
                    final long id = generatedKeys.getLong(1);
                    result.add(new SalaryDto(id, salary.getMoney(), salary.getDept()));
                }
                log.info("salary saved: {}", result);
                connection.commit();
                return result;
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            log.error("fail to save salary:{}", salaries, e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("fail close connection", e);
                }
            }
        }
    }

    @Override
    public boolean delete(long id) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("delete from salary where id = ?")) {
                statement.setLong(1, id);
                final int count = statement.executeUpdate();
                connection.commit();
                return count > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("fail close connection", e);
                }
            }
        }
    }

    @Override
    public SalaryDto get(long id) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("select * from salary where id = ?")) {
                statement.setLong(1, id);
                final ResultSet resultSet = statement.executeQuery();
                connection.commit();
                final boolean exist = resultSet.next();
                if (!exist) {
                    return null;
                }
                final long resultId = resultSet.getLong("id");
                final String dept = resultSet.getString("dept");
                final int money = resultSet.getInt("money");
                return new SalaryDto(resultId, money, dept);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("fail close connection", e);
                }
            }
        }
    }

    @Override
    public boolean update(SalaryDto salary) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("update salary set dept = ?, money = ? where id = ?")) {
                statement.setString(1, salary.getDept());
                statement.setInt(2, salary.getMoney());
                statement.setLong(3, salary.getId());
                final int count = statement.executeUpdate();
                connection.commit();
                return count > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("fail close connection", e);
                }
            }
        }
    }

    @Override
    public List<SalaryDto> findByDept(String dept) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("select * from salary where dept = ?")) {
                statement.setString(1, dept);
                final ResultSet resultSet = statement.executeQuery();
                final ArrayList<SalaryDto> result = new ArrayList<>();
                while (resultSet.next()) {
                    final long id = resultSet.getLong("id");
                    final String resultDept = resultSet.getString("dept");
                    final int money = resultSet.getInt("money");
                    result.add(new SalaryDto(id, money, resultDept));
                }
                connection.commit();
                return result;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("fail close connection", e);
                }
            }
        }
    }
}
