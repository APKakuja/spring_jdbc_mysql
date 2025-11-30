package com.ra2.Aav2.controller;

import com.ra2.Aav2.model.Customer;
import com.ra2.Aav2.repository.CustomerRepository;
import com.ra2.Aav2.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private CustomerRepository repo;



    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Customer> list = repo.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Customer> c = repo.findById(id);
        if (c.isEmpty()) return ResponseEntity.status(404).body("Usuari no trobat");
        return ResponseEntity.ok(c.get());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer c) {
        try {
            service.validateCustomerForSave(c);
            repo.save(c);
            return ResponseEntity.ok("Customer creat correctament.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFull(@PathVariable Long id, @RequestBody Customer updated) {
        if (!service.existsById(id)) return ResponseEntity.status(404).body("No trobat");
        repo.updateFull(id, updated);
        return ResponseEntity.ok(repo.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePartial(@PathVariable Long id, @RequestBody Customer partial) {
        if (!service.existsById(id)) return ResponseEntity.status(404).body("No trobat");
        String name = partial.getName() == null ? repo.findById(id).get().getName() : partial.getName();
        Integer age = partial.getAge() == null ? repo.findById(id).get().getAge() : partial.getAge();
        repo.updatePartial(id, name, age);
        return ResponseEntity.ok(repo.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (!service.existsById(id)) return ResponseEntity.status(404).body("No trobat");
        repo.deleteById(id);
        return ResponseEntity.ok("S'ha eliminat l'usuari amb id " + id);
    }


    // Endpoint per pujar imatge
    @PostMapping("/{user_id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable("user_id") Long userId,
                                         @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            return service.saveUserImage(userId, imageFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processant la imatge: " + e.getMessage());
        }
    }

    // Upload CSV massiu
    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCsv(@RequestParam("csvFile") MultipartFile csvFile) {
        try {
            return service.uploadCsv(csvFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processant el CSV: " + e.getMessage());
        }
    }

    // Upload JSON massiu
    @PostMapping("/upload-json")
    public ResponseEntity<?> uploadJson(@RequestParam("jsonFile") MultipartFile jsonFile) {
        try {
            return service.uploadJson(jsonFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processant el JSON: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<?> getUserImage(@PathVariable Long id) {
        try {
            Optional<Customer> opt = repo.findById(id);
            if (opt.isEmpty())
                return ResponseEntity.status(404).body("Usuari no trobat");

            Customer c = opt.get();

            if (c.getImagePath() == null)
                return ResponseEntity.status(404).body("L'usuari no té imatge assignada");

            Path path = Path.of("src/main/resources/private/images")
                    .resolve(Path.of(c.getImagePath()).getFileName().toString());

            if (!Files.exists(path))
                return ResponseEntity.status(404).body("La imatge no existeix al servidor");

            byte[] bytes = Files.readAllBytes(path);

            String contentType = Files.probeContentType(path);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity
                    .ok()
                    .header("Content-Type", contentType)
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obtenint la imatge: " + e.getMessage());
        }
    }

}
