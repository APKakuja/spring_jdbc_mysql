package com.ra2.Aav2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ra2.Aav2.model.Customer;
import com.ra2.Aav2.service.CustomerService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    private final String serverBaseUrl = "http://localhost:8080";

    @PostMapping
    public ResponseEntity<String> createTenCustomers(@RequestBody Customer template) {
        int inserted = customerService.createTenCustomers(template);
        return ResponseEntity.ok("S'han inserit " + inserted + " alumnes correctament.");
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        List<Customer> list = customerService.getAll();
        if (list.isEmpty()) return ResponseEntity.ok(null);
        return ResponseEntity.ok(list);
    }

    // Obtener usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        Customer c = customerService.getById(id);
        if (c == null) return ResponseEntity.ok(null);
        return ResponseEntity.ok(c);
    }

    // Update completo
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateFull(@PathVariable Long id, @RequestBody Customer updated) {
        Customer c = customerService.updateFull(id, updated);
        if (c == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(c);
    }

    // Update parcial
    @PatchMapping("/{id}/age")
    public ResponseEntity<Customer> updateAge(@PathVariable Long id,
                                              @RequestParam(required = false) String name,
                                              @RequestParam Integer age) {
        Customer c = customerService.updateAge(id, name, age);
        if (c == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(c);
    }

    // Delete con aviso
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        boolean deleted = customerService.deleteById(id);
        if (!deleted) return ResponseEntity.status(404).body("No s'ha trobat el customer amb id " + id);
        return ResponseEntity.ok("Customer amb id " + id + " eliminat correctament.");
    }

    // Nuevo endpoint: subir imagen
    @PostMapping("/{user_id}/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @PathVariable("user_id") Long userId,
            @RequestParam("imageFile") MultipartFile imageFile
    ) {
        return customerService.saveCustomerImage(userId, imageFile, serverBaseUrl);
    }

    // Nuevo endpoint: carga masiva CSV
    @PostMapping("/upload-csv")
    public ResponseEntity<Map<String, Object>> uploadCsv(
            @RequestParam("csvFile") MultipartFile csvFile
    ) {
        return customerService.uploadCustomersCsv(csvFile);
    }

    // Nuevo endpoint: carga masiva JSON
    @PostMapping("/upload-json")
    public ResponseEntity<Map<String, Object>> uploadJson(
            @RequestParam("jsonFile") MultipartFile jsonFile
    ) {
        return customerService.uploadCustomersJson(jsonFile);
    }
}
