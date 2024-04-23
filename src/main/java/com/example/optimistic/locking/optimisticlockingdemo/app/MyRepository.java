package com.example.optimistic.locking.optimisticlockingdemo.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyRepository extends JpaRepository<MyTable, Long> {

    @Meta(comment="Find product by product name")
    Optional<MyTable> findByProductName(String name);
}
