package com.ra2.Aav2.repository;

import com.ra2.Aav2.model.Customer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper: mapea columnas de BD a la clase Customer
    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer c = new Customer();
            c.setId(rs.getLong("id"));
            c.setName(rs.getString("name"));
            c.setDescription(rs.getString("description"));
            c.setAge(rs.getInt("age"));
            c.setCourse(rs.getString("course"));
            c.setPassword(rs.getString("password"));
            c.setDataCreated(rs.getInt("data_created"));
            c.setDataUpdated(rs.getInt("data_updated"));
            c.setImagePath(rs.getString("image_path"));
            return c;
        }
    }

    // Crear tabla (ajustada a los campos del modelo y al ejercicio)
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS customer (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "description VARCHAR(500)," +
                "age INT," +
                "course VARCHAR(100)," +
                "password VARCHAR(255)," +
                "data_created INT," +
                "data_updated INT," +
                "image_path VARCHAR(500) NULL" +
                ")";
        jdbcTemplate.execute(sql);
    }

    // Insertar un registro (save)
    public int save(Customer c) {
        String sql = "INSERT INTO customer (name, description, age, course, password, data_created, data_updated, image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                c.getName(),
                c.getDescription(),
                c.getAge(),
                c.getCourse(),
                c.getPassword(),
                c.getDataCreated(),
                c.getDataUpdated(),
                c.getImagePath()
        );
    }

    // Encontrar todos
    public List<Customer> findAll() {
        String sql = "SELECT id, name, description, age, course, password, data_created, data_updated, image_path FROM customer";
        return jdbcTemplate.query(sql, new CustomerRowMapper());
    }

    // Encontrar por id
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT id, name, description, age, course, password, data_created, data_updated, image_path " +
                "FROM customer WHERE id = ?";
        try {
            Customer c = jdbcTemplate.queryForObject(sql, new CustomerRowMapper(), id);
            return Optional.ofNullable(c);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Update completo
    public int updateFull(Long id, Customer updated) {
        String sql = "UPDATE customer SET " +
                "name = ?, description = ?, age = ?, course = ?, password = ?, " +
                "data_created = ?, data_updated = ?, image_path = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql,
                updated.getName(),
                updated.getDescription(),
                updated.getAge(),
                updated.getCourse(),
                updated.getPassword(),
                updated.getDataCreated(),
                updated.getDataUpdated(),
                updated.getImagePath(),
                id
        );
    }

    // Update parcial: nombre y edad (según tu controller)
    public int updatePartial(Long id, String name, Integer age) {
        String sql = "UPDATE customer SET name = ?, age = ? WHERE id = ?";
        return jdbcTemplate.update(sql, name, age, id);
    }

    // Borrar por id
    public int deleteById(Long id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // Actualizar solo image_path (para el endpoint de imagen)
    public int updateImagePath(Long id, String imagePath) {
        String sql = "UPDATE customer SET image_path = ? WHERE id = ?";
        return jdbcTemplate.update(sql, imagePath, id);
    }
}
