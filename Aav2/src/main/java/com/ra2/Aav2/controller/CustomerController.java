package com.ra2.Aav2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ra2.Aav2.repository.CustomerRepository;
import com.ra2.Aav2.model.Customer;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerRepository repo;

    @PostMapping
    public ResponseEntity<String> createTenCustomers(@RequestBody Customer template) {
        int inserted = 0;
        if (template == null) template = new Customer();
        for (int i = 1; i <= 10; i++) {
            Customer c = new Customer();
            c.setName((template.getName() != null ? template.getName() : "Alumne") + " " + i);
            c.setDescription((template.getDescription() != null ? template.getDescription() : "Descripció") + " " + i);
            c.setAge(template.getAge() != 0 ? template.getAge() + (i % 5) : 16 + (i % 5));
            c.setCourse(template.getCourse() != null ? template.getCourse() + " " + ((i % 3) + 1) : "Curs " + ((i % 3) + 1));
            c.setPassword(template.getPassword() != null ? template.getPassword() : "pass" + i);
            int result = repo.save(c);
            if (result > 0) inserted++;
        }
        return ResponseEntity.ok("S'han inserit " + inserted + " alumnes correctament.");
    }


    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        List<Customer> list = repo.findAll();
        if (list.isEmpty()) return ResponseEntity.ok(null); // seguint l'enunciat: retornar null si no troba cap usuari
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null)); // retornar null si no existeix (segons enunciat)
    }


    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateFull(@PathVariable Long id, @RequestBody Customer updated) {
        if (repo.findById(id).isEmpty())
            return ResponseEntity.status(404).build();
        repo.updateFull(id, updated);
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(500).build());
    }


    @PatchMapping("/{id}/age")
    public ResponseEntity<Customer> updateAge(@PathVariable Long id, @RequestParam(required = false) String name, @RequestParam Integer age) {
        if (repo.findById(id).isEmpty()) return ResponseEntity.status(404).build();
        String newName = name != null ? name : repo.findById(id).get().getName();
        repo.updatePartial(id, newName, age);
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(500).build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (repo.findById(id).isEmpty())
            return ResponseEntity.status(404).body("No s'ha trobat el customer amb id " + id);
        repo.deleteById(id);
        return ResponseEntity.ok("Customer amb id " + id + " eliminat correctament.");
    }
}
