package com.ra2.Aav2.repository;

import com.ra2.Aav2.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Insert amb columnes explicites (no afegim timestamps -> DB posa per defecte)
    public int save(Customer c) {
        String sql = "INSERT INTO customer (name, description, age, course, email, password, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                c.getName(),
                c.getDescription(),
                c.getAge(),
                c.getCourse(),
                c.getEmail(),
                c.getPassword(),
                c.getImagePath()
        );
    }

    public List<Customer> findAll() {
        String sql = "SELECT * FROM customer";
        return jdbcTemplate.query(sql, new CustomerRowMapper());
    }

    public Optional<Customer> findById(Long id) {
        String sql = "SELECT * FROM customer WHERE id = ?";
        List<Customer> list = jdbcTemplate.query(sql, new Object[]{id}, new CustomerRowMapper());
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

    public int updateFull(Long id, Customer c) {
        String sql = "UPDATE customer SET name = ?, description = ?, age = ?, course = ?, email = ?, password = ?, data_updated = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql,
                c.getName(),
                c.getDescription(),
                c.getAge(),
                c.getCourse(),
                c.getEmail(),
                c.getPassword(),
                id
        );
    }

    public int updatePartial(Long id, String name, Integer age) {
        String sql = "UPDATE customer SET name = ?, age = ?, data_updated = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql, name, age, id);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // NOVA: actualitzar image_path
    public int updateImagePath(Long id, String imagePath) {
        String sql = "UPDATE customer SET image_path = ?, data_updated = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql, imagePath, id);
    }
}
