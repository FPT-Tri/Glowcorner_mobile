package com.prm392.assignment.productsale.model.products;

import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductSortAndFilterModel {
    public static final String ORDER_BY_PRICE = "price";
    public static final double PRICE_MIN = 0;
    public static final double PRICE_MAX = 1000000;

    private int pageIndex, pageSize;
    private String sortBy;
    private Boolean sortDescending;
    private Double minPrice, maxPrice;
    private HashSet<Integer> categories;

    public ProductSortAndFilterModel() {
        minPrice = PRICE_MIN;
        maxPrice = PRICE_MAX;
        categories = new HashSet<>();
    }
}
