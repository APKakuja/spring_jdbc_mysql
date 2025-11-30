package com.ra2.Aav2;

import com.ra2.Aav2.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Aav2Application implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(Aav2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        customerRepository.createTable();
        System.out.println("Tabla 'customer' verificada/creada correctamente.");
    }
}
