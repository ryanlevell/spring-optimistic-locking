package com.example.optimistic.locking.optimisticlockingdemo.app;

import lombok.RequiredArgsConstructor;
import org.hibernate.StaleStateException;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

    final MyRepository myRepository;

    @GetMapping("/")
    @Retryable(retryFor = StaleStateException.class)
    public ResponseEntity<?> addProduct() {

        MyTable table1 = myRepository.findByProductName("Keyboard").get();
        table1.quantity = table1.quantity + 1;
        myRepository.save(table1);

        return ResponseEntity.ok().body(table1);
    }
}
