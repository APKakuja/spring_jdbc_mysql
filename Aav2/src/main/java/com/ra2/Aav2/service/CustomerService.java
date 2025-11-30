package com.ra2.Aav2.service;

import com.ra2.Aav2.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CustomerService {

    // Validacions generals
    void validateCustomerForSave(Customer c) throws IllegalArgumentException;

    boolean existsById(Long id);


    ResponseEntity<?> saveUserImage(Long userId, MultipartFile imageFile) throws IOException;


    ResponseEntity<?> uploadCsv(MultipartFile csvFile) throws IOException;


    ResponseEntity<?> uploadJson(MultipartFile jsonFile) throws IOException;
}
