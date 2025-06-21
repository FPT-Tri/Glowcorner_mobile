package com.prm392.assignment.productsale.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettingsItemModel {
    private String name;
    private String value;

    public SettingsItemModel(){}

    public SettingsItemModel(String name, String value){
        this.name = name;
        this.value = value;
    }

}
