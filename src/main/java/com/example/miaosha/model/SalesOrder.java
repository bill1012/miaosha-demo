package com.example.miaosha.model;

import lombok.Data;

import java.util.Date;

@Data
public class SalesOrder {
    private Long id;
    private Long cid;
    private String name;
    private Date createTime;

}
