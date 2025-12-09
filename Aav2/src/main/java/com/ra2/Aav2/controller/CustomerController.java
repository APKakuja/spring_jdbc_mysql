package com.ra2.Aav2.controller;

import com.ra2.Aav2.model.Customer;
import com.ra2.Aav2.repository.CustomerRepository;
import com.ra2.Aav2.service.CustomerService;
import com.ra2.Aav2.logging.CustomLogging;
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
        CustomLogging.info("CustomerController", "getAll", "Accés a endpoint GET /api/users");
        List<Customer> list = repo.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        CustomLogging.info("CustomerController", "getById", "Accés a endpoint amb id=" + id);
        Optional<Customer> c = repo.findById(id);
        if (c.isEmpty()) {
            CustomLogging.error("CustomerController", "getById", "Usuari no trobat amb id=" + id);
            return ResponseEntity.status(404).body("Usuari no trobat");
        }
        return ResponseEntity.ok(c.get());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer c) {
        CustomLogging.info("CustomerController", "createCustomer", "Intent de creació de nou usuari");
        try {
            service.validateCustomerForSave(c);
            repo.save(c);
            CustomLogging.info("CustomerController", "createCustomer", "Usuari creat correctament: " + c.getName());
            return ResponseEntity.ok("Customer creat correctament.");
        } catch (IllegalArgumentException ex) {
            CustomLogging.error("CustomerController", "createCustomer", "Error creant usuari: " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFull(@PathVariable Long id, @RequestBody Customer updated) {
        CustomLogging.info("CustomerController", "updateFull", "Intent d'actualització completa per id=" + id);
        if (!service.existsById(id)) {
            CustomLogging.error("CustomerController", "updateFull", "Usuari no trobat per id=" + id);
            return ResponseEntity.status(404).body("No trobat");
        }
        repo.updateFull(id, updated);
        CustomLogging.info("CustomerController", "updateFull", "Usuari actualitzat completament per id=" + id);
        return ResponseEntity.ok(repo.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePartial(@PathVariable Long id, @RequestBody Customer partial) {
        CustomLogging.info("CustomerController", "updatePartial", "Intent d'actualització parcial per id=" + id);
        if (!service.existsById(id)) {
            CustomLogging.error("CustomerController", "updatePartial", "Usuari no trobat per id=" + id);
            return ResponseEntity.status(404).body("No trobat");
        }
        String name = partial.getName() == null ? repo.findById(id).get().getName() : partial.getName();
        Integer age = partial.getAge() == null ? repo.findById(id).get().getAge() : partial.getAge();
        repo.updatePartial(id, name, age);
        CustomLogging.info("CustomerController", "updatePartial", "Usuari actualitzat parcialment per id=" + id);
        return ResponseEntity.ok(repo.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        CustomLogging.info("CustomerController", "deleteById", "Intent d'eliminació per id=" + id);
        if (!service.existsById(id)) {
            CustomLogging.error("CustomerController", "deleteById", "Usuari no trobat per id=" + id);
            return ResponseEntity.status(404).body("No trobat");
        }
        repo.deleteById(id);
        CustomLogging.info("CustomerController", "deleteById", "Usuari eliminat correctament per id=" + id);
        return ResponseEntity.ok("S'ha eliminat l'usuari amb id " + id);
    }

    // Endpoint per pujar imatge
    @PostMapping(path = "/{user_id}/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImage(@PathVariable("user_id") Long userId,
                                         @RequestParam("imageFile") MultipartFile imageFile) {
        CustomLogging.info("CustomerController", "uploadImage", "Accés a endpoint per pujar imatge a usuari id=" + userId);
        try {
            return service.saveUserImage(userId, imageFile);
        } catch (IOException e) {
            CustomLogging.error("CustomerController", "uploadImage", "Error processant imatge: " + e.getMessage());
            return ResponseEntity.status(500).body("Error processant la imatge: " + e.getMessage());
        }
    }

    // Upload CSV massiu
    @PostMapping(path = "/upload-csv", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCsv(@RequestParam("csvFile") MultipartFile csvFile) {
        CustomLogging.info("CustomerController", "uploadCsv", "Accés a endpoint per càrrega CSV");
        try {
            return service.uploadCsv(csvFile);
        } catch (IOException e) {
            CustomLogging.error("CustomerController", "uploadCsv", "Error processant CSV: " + e.getMessage());
            return ResponseEntity.status(500).body("Error processant el CSV: " + e.getMessage());
        }
    }

    // Upload JSON massiu
    @PostMapping(path = "/upload-json", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadJson(@RequestParam("jsonFile") MultipartFile jsonFile) {
        CustomLogging.info("CustomerController", "uploadJson", "Accés a endpoint per càrrega JSON");
        try {
            return service.uploadJson(jsonFile);
        } catch (IOException e) {
            CustomLogging.error("CustomerController", "uploadJson", "Error processant JSON: " + e.getMessage());
            return ResponseEntity.status(500).body("Error processant el JSON: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<?> getUserImage(@PathVariable Long id) {
        CustomLogging.info("CustomerController", "getUserImage", "Accés a endpoint per obtenir imatge usuari id=" + id);
        try {
            Optional<Customer> opt = repo.findById(id);
            if (opt.isEmpty()) {
                CustomLogging.error("CustomerController", "getUserImage", "Usuari no trobat per id=" + id);
                return ResponseEntity.status(404).body("Usuari no trobat");
            }

            Customer c = opt.get();

            if (c.getImagePath() == null) {
                CustomLogging.error("CustomerController", "getUserImage", "Usuari sense imatge assignada id=" + id);
                return ResponseEntity.status(404).body("L'usuari no té imatge assignada");
            }

            Path path = Path.of("src/main/resources/private/images")
                    .resolve(Path.of(c.getImagePath()).getFileName().toString());

            if (!Files.exists(path)) {
                CustomLogging.error("CustomerController", "getUserImage", "Imatge no existeix al servidor per id=" + id);
                return ResponseEntity.status(404).body("La imatge no existeix al servidor");
            }

            byte[] bytes = Files.readAllBytes(path);

            String contentType = Files.probeContentType(path);
            if (contentType == null) contentType = "application/octet-stream";

            CustomLogging.info("CustomerController", "getUserImage", "Imatge retornada correctament per usuari id=" + id);
            return ResponseEntity
                    .ok()
                    .header("Content-Type", contentType)
                    .body(bytes);

        } catch (Exception e) {
            CustomLogging.error("CustomerController", "getUserImage", "Error obtenint imatge: " + e.getMessage());
            return ResponseEntity.status(500).body("Error obtenint la imatge: " + e.getMessage());
        }
    }
}
