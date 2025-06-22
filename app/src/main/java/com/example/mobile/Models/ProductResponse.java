package com.example.mobile.Models;

import java.util.List;

public class ProductResponse {
    private List<Product> data;
    private String status;

    public List<Product> getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
