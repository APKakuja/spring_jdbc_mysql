package com.ra2.Aav2.service;

import com.ra2.Aav2.model.Customer;
import com.ra2.Aav2.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.*;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    private static final Path IMAGES_DIR = Paths.get("src/main/resources/public/images");
    private static final Path CSV_PROCESSED_DIR = Paths.get("src/main/resources/public/csv_processed");
    private static final Path JSON_PROCESSED_DIR = Paths.get("src/main/resources/public/json_processed");

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    // --- VALIDACIONES ---
    private void validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException("El nom ha de tenir mínim 3 caràcters.");
        }
    }

    // --- CRUD ---
    public int createTenCustomers(Customer template) {
        int inserted = 0;
        if (template == null) template = new Customer();
        for (int i = 1; i <= 10; i++) {
            Customer c = new Customer();
            c.setName((template.getName() != null ? template.getName() : "Alumne") + " " + i);
            c.setDescription((template.getDescription() != null ? template.getDescription() : "Descripció") + " " + i);
            c.setAge(template.getAge() != 0 ? template.getAge() + (i % 5) : 16 + (i % 5));
            c.setCourse(template.getCourse() != null ? template.getCourse() + " " + ((i % 3) + 1) : "Curs " + ((i % 3) + 1));
            c.setPassword(template.getPassword() != null ? template.getPassword() : "pass" + i);
            validateName(c.getName());
            int result = repo.save(c);
            if (result > 0) inserted++;
        }
        return inserted;
    }

    public List<Customer> getAll() {
        return repo.findAll();
    }

    public Customer getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Customer updateFull(Long id, Customer updated) {
        if (repo.findById(id).isEmpty()) return null;
        validateName(updated.getName());
        repo.updateFull(id, updated);
        return repo.findById(id).orElse(null);
    }

    public Customer updateAge(Long id, String name, Integer age) {
        if (repo.findById(id).isEmpty()) return null;
        String newName = name != null ? name : repo.findById(id).get().getName();
        validateName(newName);
        repo.updatePartial(id, newName, age);
        return repo.findById(id).orElse(null);
    }

    public boolean deleteById(Long id) {
        if (repo.findById(id).isEmpty()) return false;
        repo.deleteById(id);
        return true;
    }

    // --- IMAGEN ---
    public ResponseEntity<Map<String, Object>> saveCustomerImage(Long id, MultipartFile imageFile, String serverBaseUrl) {
        try {
            Customer customer = getById(id);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuari no trobat", "id", id));
            }

            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "No s'ha rebut cap imatge"));
            }

            String originalName = imageFile.getOriginalFilename();
            if (originalName == null || !originalName.toLowerCase().endsWith(".jpg")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "El fitxer ha de ser .jpg"));
            }

            Files.createDirectories(IMAGES_DIR);
            String fileName = "customer-" + id + "-" + System.currentTimeMillis() + ".jpg";
            Path target = IMAGES_DIR.resolve(fileName);
            Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = "/images/" + fileName;
            repo.updateImagePath(id, relativePath);

            String imageUrl = serverBaseUrl + relativePath;

            return ResponseEntity.ok(Map.of(
                    "message", "Imatge pujada correctament",
                    "userId", id,
                    "path", relativePath,
                    "imageUrl", imageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error guardant la imatge", "error", e.getMessage()));
        }
    }

    // --- CSV ---
    public ResponseEntity<Map<String, Object>> uploadCustomersCsv(MultipartFile csvFile) {
        if (csvFile == null || csvFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "No s'ha rebut cap fitxer CSV"));
        }

        int inserted = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    errors.add("Línia " + lineNum + ": format incorrecte");
                    continue;
                }
                try {
                    Customer c = new Customer();
                    c.setName(parts[0].trim());
                    c.setDescription(parts[1].trim());
                    c.setAge(Integer.parseInt(parts[2].trim()));
                    c.setCourse(parts[3].trim());
                    c.setPassword(parts[4].trim());
                    validateName(c.getName());
                    repo.save(c);
                    inserted++;
                } catch (Exception ex) {
                    errors.add("Línia " + lineNum + ": " + ex.getMessage());
                }
            }

            Files.createDirectories(CSV_PROCESSED_DIR);
            String processedName = "customers-" + System.currentTimeMillis() + ".csv";
            Path processedTarget = CSV_PROCESSED_DIR.resolve(processedName);
            Files.copy(csvFile.getInputStream(), processedTarget, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(Map.of(
                    "message", "CSV processat",
                    "inserted", inserted,
                    "errors", errors,
                    "processedPath", "/csv_processed/" + processedName
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error processant CSV", "error", e.getMessage()));
        }
    }

    // --- JSON ---
    public ResponseEntity<Map<String, Object>> uploadCustomersJson(MultipartFile jsonFile) {
        if (jsonFile == null || jsonFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "No s'ha rebut cap fitxer JSON"));
        }

        int inserted = 0;
        List<String> errors = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> root = mapper.readValue(jsonFile.getInputStream(), Map.class);

            Map<?, ?> data = (Map<?, ?>) root.get("data");
            List<?> usersList = (List<?>) data.get("users");

            for (int i = 0; i < usersList.size(); i++) {
                Map<?, ?> u = (Map<?, ?>) usersList.get(i);
                try {
                    Customer c = new Customer();
                    c.setName(String.valueOf(u.get("name")));
                    c.setDescription(String.valueOf(u.get("description")));
                    c.setCourse(String.valueOf(u.get("course")));
                    c.setPassword(String.valueOf(u.get("password")));
                    c.setAge(0);
                    validateName(c.getName());
                    repo.save(c);
                    inserted++;
                } catch (Exception ex) {
                    errors.add("Usuari " + (i + 1) + ": " + ex.getMessage());
                }
            }

            Files.createDirectories(JSON_PROCESSED_DIR);
            String processedName = "customers-" + System.currentTimeMillis() + ".json";
            Path processedTarget = JSON_PROCESSED_DIR.resolve(processedName);
            Files.copy(jsonFile.getInputStream(), processedTarget, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(Map.of(
                    "message", "JSON processat",
                    "inserted", inserted,
                    "errors", errors,
                    "processedPath", "/json_processed/" + processedName
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error processant JSON", "error", e.getMessage()));
        }
    }
}
