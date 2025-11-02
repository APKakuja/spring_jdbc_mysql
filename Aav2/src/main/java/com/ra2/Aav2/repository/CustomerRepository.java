package com.ra2.Aav2.repository;

import com.ra2.Aav2.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
 public class CustomerRepository {

        @Autowired
        private JdbcTemplate jdbcTemplate;


        public int save(Customer c) {
            String sql = "INSERT INTO customer (name, description, age, course, password, data_created, data_updated) VALUES (?, ?, ?, ?, ?, ?, ?)";
            Timestamp now = new Timestamp(System.currentTimeMillis());
            return jdbcTemplate.update(sql, c.getName(), c.getDescription(), c.getAge(), c.getCourse(), c.getPassword(), now, now);
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
            String sql = "UPDATE customer SET name=?, description=?, age=?, course=?, password=?, data_updated=? WHERE id=?";
            Timestamp now = new Timestamp(System.currentTimeMillis());
            return jdbcTemplate.update(sql, c.getName(), c.getDescription(), c.getAge(), c.getCourse(), c.getPassword(), now, id);
        }

        public int updatePartial(Long id, String name, Integer age) {
            String sql = "UPDATE customer SET name=?, age=?, data_updated=? WHERE id=?";
            Timestamp now = new Timestamp(System.currentTimeMillis());
            return jdbcTemplate.update(sql, name, age, now, id);
        }


        public int deleteById(Long id) {
            String sql = "DELETE FROM customer WHERE id=?";
            return jdbcTemplate.update(sql, id);
        }
    }
