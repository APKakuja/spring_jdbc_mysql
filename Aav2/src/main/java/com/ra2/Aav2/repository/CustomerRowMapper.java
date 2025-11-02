package com.ra2.Aav2.repository;

import com.ra2.Aav2.model.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setAge(rs.getInt("age"));
        c.setCourse(rs.getString("course"));
        c.setPassword(rs.getString("password"));
        c.setDataCreated(rs.getTimestamp("data_created"));
        c.setDataUpdated(rs.getTimestamp("data_updated"));
        return c;
    }
}