package com.example.miaosha.model;

import lombok.Data;

@Data
public class Catalog {
    private Long id;
    private String name;
    private Long total;
    private Long sold;
    private Long version;
}
