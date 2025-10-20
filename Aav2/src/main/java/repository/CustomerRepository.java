package repository;

import model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper para convertir ResultSet a Customer
    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("age"),
                    rs.getString("cicle"),
                    rs.getInt("year_val")
            );
        }
    }

    // Crear tabla
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS customer (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "email VARCHAR(255)," +
                "age INT," +
                "cicle VARCHAR(50)," +
                "year_val INT)"; // <-- usa year_val
        jdbcTemplate.execute(sql);
    }
}