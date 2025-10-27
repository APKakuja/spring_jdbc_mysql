package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.CustomerRepository;
import model.Customer;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository repo;

    // POST: crea 10 alumnes y devuelve mensaje
    @PostMapping
    public ResponseEntity<String> createTenCustomers() {
        int inserted = 0;
        for (int i = 1; i <= 10; i++) {
            Customer c = new Customer();
            c.setName("Alumne " + i);
            c.setDescription("Descripció alumne " + i);
            c.setAge(16 + (i % 5));
            c.setCourse("Curs " + ((i % 3) + 1));
            int result = repo.save(c);
            if (result > 0) inserted++;
        }
        return ResponseEntity.ok("S'han inserit " + inserted + " alumnes correctament.");
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    // GET by id -> devuelve Customer o 404
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    // PUT -> actualiza totalmente y devuelve el Customer actualizado
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateFull(@PathVariable Long id, @RequestBody Customer updated) {
        if (repo.findById(id).isEmpty())
            return ResponseEntity.status(404).build();
        repo.updateFull(id, updated);
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(500).build()); // improbable
    }

    // PATCH -> actualiza parcialmente y devuelve el Customer actualizado
    @PatchMapping("/{id}")
    public ResponseEntity<Customer> updatePartial(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        var existing = repo.findById(id);
        if (existing.isEmpty()) return ResponseEntity.status(404).build();

        String name = updates.containsKey("name") ? updates.get("name").toString() : existing.get().getName();
        Integer age = updates.containsKey("age") ? Integer.valueOf(updates.get("age").toString()) : existing.get().getAge();
        repo.updatePartial(id, name, age);
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(500).build());
    }

    // DELETE -> mensaje
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (repo.findById(id).isEmpty())
            return ResponseEntity.status(404).body("No s'ha trobat el customer amb id " + id);
        repo.deleteById(id);
        return ResponseEntity.ok("Customer amb id " + id + " eliminat correctament.");
    }
}
