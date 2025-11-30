package com.ra2.Aav2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.Aav2.model.Customer;
import com.ra2.Aav2.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository repo;

    private final Path imagesBase = Paths.get("src/main/resources/private/images");
    private final Path csvProcessed = Paths.get("src/main/resources/csv_processed");
    private final Path jsonProcessed = Paths.get("src/main/resources/json_processed");

    public CustomerServiceImpl() {
        try {
            Files.createDirectories(imagesBase);
            Files.createDirectories(csvProcessed);
            Files.createDirectories(jsonProcessed);
        } catch (IOException e) {
            // Si falla la creació de carpetes
        }
    }

    @Override
    public void validateCustomerForSave(Customer c) throws IllegalArgumentException {
        if (c.getName() == null || c.getName().trim().length() < 3) {
            throw new IllegalArgumentException("El nom ha de tenir almenys 3 caràcters.");
        }
    }

    @Override
    public boolean existsById(Long id) {
        return repo.findById(id).isPresent();
    }

    @Override
    public ResponseEntity<?> saveUserImage(Long userId, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No s'ha subministrat cap fitxer o està buit.");
        }

        String original = imageFile.getOriginalFilename();
        if (original == null) original = "image";
        String lower = original.toLowerCase();
        if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png"))) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Només s'admeten imatges JPG/PNG.");
        }

        // Comprovar existència usuari
        if (!existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari amb id " + userId + " no trobat.");
        }

        // Crear nom únic: userId_timestamp_original
        String filename = "user_" + userId + "_" + Instant.now().toEpochMilli() + "_" + original.replaceAll("\\s+", "_");
        Path dest = imagesBase.resolve(filename);

        // Guardar fitxer amb NIO2
        try (InputStream is = imageFile.getInputStream()) {
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en desar la imatge: " + e.getMessage());
        }

        String storedPath = "/public/images/" + filename;
        int updated = repo.updateImagePath(userId, storedPath);
        if (updated <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No s'ha pogut actualitzar la ruta a la BD.");
        }

        return ResponseEntity.ok(storedPath);
    }

    @Override
    public ResponseEntity<?> uploadCsv(MultipartFile csvFile) throws IOException {
        if (csvFile == null || csvFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Fitxer CSV buit o no proporcionat.");
        }

        int inserted = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String header = br.readLine(); // llegir header (pot ser null)
            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue; // saltar línies mal formades
                Customer c = new Customer();
                c.setName(parts[0].trim());
                c.setDescription(parts[1].trim());
                String ageStr = parts[2].trim();
                c.setAge(ageStr.isEmpty() ? null : Integer.valueOf(ageStr));
                c.setCourse(parts[3].trim());
                c.setEmail(parts[4].trim());
                c.setPassword(parts[5].trim());

                try {
                    validateCustomerForSave(c);
                    repo.save(c);
                    inserted++;
                } catch (IllegalArgumentException ex) {
                    // si falla validació, saltem el registre
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error llegint CSV: " + e.getMessage());
        }

        // Desar el fitxer original a csv_processed amb un nom únic
        String fileName = "csv_" + Instant.now().toEpochMilli() + "_" + (csvFile.getOriginalFilename() == null ? "upload.csv" : csvFile.getOriginalFilename());
        Path dest = csvProcessed.resolve(fileName);
        try (InputStream is = csvFile.getInputStream()) {
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Insertats: " + inserted + ". Error guardant fitxer: " + e.getMessage());
        }

        return ResponseEntity.ok("Registres afegits: " + inserted);
    }

    @Override
    public ResponseEntity<?> uploadJson(MultipartFile jsonFile) throws IOException {
        if (jsonFile == null || jsonFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Fitxer JSON buit o no proporcionat.");
        }

        int inserted = 0;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try (InputStream is = jsonFile.getInputStream()) {
            root = mapper.readTree(is);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON invàlid: " + e.getMessage());
        }

        JsonNode usersNode = root.path("data").path("users");
        if (!usersNode.isArray()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON no té el format esperat (data.users array).");
        }

        for (JsonNode u : usersNode) {
            Customer c = new Customer();
            c.setName(u.path("name").asText(""));
            c.setDescription(u.path("description").asText(""));
            c.setEmail(u.path("email").asText(""));
            c.setPassword(u.path("password").asText(""));
            // edat/curso no proporcionats en JSON; deixar null o posar valors per defecte
            try {
                validateCustomerForSave(c);
                repo.save(c);
                inserted++;
            } catch (IllegalArgumentException ex) {
                // saltar
            }
        }

        // Desar el fitxer original a json_processed
        String fileName = "json_" + Instant.now().toEpochMilli() + "_" + (jsonFile.getOriginalFilename() == null ? "upload.json" : jsonFile.getOriginalFilename());
        Path dest = jsonProcessed.resolve(fileName);
        try (InputStream is = jsonFile.getInputStream()) {
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Insertats: " + inserted + ". Error guardant fitxer JSON: " + e.getMessage());
        }

        return ResponseEntity.ok("Registres afegits: " + inserted);
    }
}
