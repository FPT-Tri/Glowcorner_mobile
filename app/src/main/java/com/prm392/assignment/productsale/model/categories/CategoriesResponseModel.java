package com.prm392.assignment.productsale.model.categories;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoriesResponseModel {

    @SerializedName("categories")
    private ArrayList<CategoryModel> categories;
}
