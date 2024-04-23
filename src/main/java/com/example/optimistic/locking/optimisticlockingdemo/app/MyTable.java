package com.example.optimistic.locking.optimisticlockingdemo.app;

import jakarta.persistence.*;

@Entity
public class MyTable {

    @Id
    public long id;

    @Version
    public long version;

    public String productName;

    public long quantity;
}
