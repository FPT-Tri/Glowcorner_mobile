package com.prm392.assignment.productsale.view.fragment.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.FragmentProductSortAndFilterDialogBinding;
import com.prm392.assignment.productsale.model.categories.CategoryModel;
import com.prm392.assignment.productsale.model.products.ProductSortAndFilterModel;

import java.util.ArrayList;

import lombok.Setter;

public class ProductSortAndFilterDialog extends BottomSheetDialogFragment {

    @Setter
    ProductSortAndFilterDialog.DialogResultListener dialogResultListener;
    private FragmentProductSortAndFilterDialogBinding vb;
    @Setter
    private ProductSortAndFilterModel sortAndFilterModel;
    @Setter
    private ArrayList<CategoryModel> categories;

    public ProductSortAndFilterDialog() {
        // Required empty public constructor
        sortAndFilterModel = new ProductSortAndFilterModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentProductSortAndFilterDialogBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.sortAndFilterDialogReset.setOnClickListener(button -> {
            sortAndFilterModel = new ProductSortAndFilterModel();
            renderData();
            updateSortAndFilterModel();
        });

        vb.productSortAndFilterDialogApplyFilter.setOnClickListener(button -> {
            updateSortAndFilterModel();
            dialogResultListener.onApply(sortAndFilterModel);
            dismiss();
        });

        renderData(); // Initial render
    }

    void renderData() {
        // Sorting
        String orderBy = sortAndFilterModel.getSortBy();
        Boolean sortDescending = sortAndFilterModel.getSortDescending();
        if (orderBy == null || sortDescending == null) {
            vb.productSortAndFilterDialogSortGroup.clearCheck();
        } else if (orderBy.equals(ProductSortAndFilterModel.ORDER_BY_PRICE) && !sortDescending) {
            vb.productSortAndFilterDialogSortGroup.check(R.id.productSortAndFilterDialog_sort_priceAsc);
        } else if (orderBy.equals(ProductSortAndFilterModel.ORDER_BY_PRICE)) {
            vb.productSortAndFilterDialogSortGroup.check(R.id.productSortAndFilterDialog_sort_priceAsc);
        }

        //Price Section
        if (sortAndFilterModel.getMinPrice() > ProductSortAndFilterModel.PRICE_MIN)
            vb.productSortAndFilterDialogMinPrice.setText(String.valueOf(sortAndFilterModel.getMinPrice()));
        else vb.productSortAndFilterDialogMinPrice.setText("");
        if (sortAndFilterModel.getMaxPrice() < ProductSortAndFilterModel.PRICE_MAX)
            vb.productSortAndFilterDialogMaxPrice.setText(String.valueOf(sortAndFilterModel.getMaxPrice()));
        else vb.productSortAndFilterDialogMaxPrice.setText("");

        vb.productSortAndFilterDialogMinPrice.clearFocus();
        vb.productSortAndFilterDialogMaxPrice.clearFocus();

        //Category Group
        vb.productSortAndFilterDialogGroup.removeAllViews();
        for (CategoryModel category : categories) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip_layout, vb.productSortAndFilterDialogGroup, false);
            chip.setText(category.getCategoryName());
            chip.setTag(category.getCategoryId());
            chip.setCheckedIconVisible(true);
            chip.setCheckedIconTintResource(R.color.lightModesecondary);

            vb.productSortAndFilterDialogGroup.addView(chip);

            if (sortAndFilterModel.getCategories().contains(category.getCategoryId()))
                chip.setChecked(true);
        }
    }

    void updateSortAndFilterModel() {
        //Sorting
        int checkedRadioButtonId = vb.productSortAndFilterDialogSortGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.productSortAndFilterDialog_sort_priceAsc) {
            sortAndFilterModel.setSortBy(ProductSortAndFilterModel.ORDER_BY_PRICE);
            sortAndFilterModel.setSortDescending(false);
        } else if (checkedRadioButtonId == R.id.productSortAndFilterDialog_sort_priceDsc) {
            sortAndFilterModel.setSortBy(ProductSortAndFilterModel.ORDER_BY_PRICE);
            sortAndFilterModel.setSortDescending(true);
        } else {
            sortAndFilterModel.setSortBy(null); // Clear sort if no radio button selected
            sortAndFilterModel.setSortDescending(null);
        }

        //Price
        String minPriceText = vb.productSortAndFilterDialogMinPrice.getText().toString();
        if (minPriceText.isEmpty()) {
            sortAndFilterModel.setMinPrice(ProductSortAndFilterModel.PRICE_MIN);
        } else {
            try {
                sortAndFilterModel.setMinPrice(Double.parseDouble(minPriceText));
            } catch (NumberFormatException e) {
                sortAndFilterModel.setMinPrice(ProductSortAndFilterModel.PRICE_MIN); // Fallback on error
            }
        }

        String maxPriceText = vb.productSortAndFilterDialogMaxPrice.getText().toString();
        if (maxPriceText.isEmpty()) {
            sortAndFilterModel.setMaxPrice(ProductSortAndFilterModel.PRICE_MAX);
        } else {
            try {
                sortAndFilterModel.setMaxPrice(Double.parseDouble(maxPriceText));
            } catch (NumberFormatException e) {
                sortAndFilterModel.setMaxPrice(ProductSortAndFilterModel.PRICE_MAX); // Fallback on error
            }
        }

        //Category
        sortAndFilterModel.getCategories().clear();
        for (int i = 0; i < vb.productSortAndFilterDialogGroup.getChildCount(); i++) {
            Chip chip = (Chip) vb.productSortAndFilterDialogGroup.getChildAt(i);
            if (chip.isChecked()) {
                int categoryId = (int) chip.getTag();
                sortAndFilterModel.getCategories().add(categoryId);
            }
        }
    }

    public interface DialogResultListener {
        void onApply(ProductSortAndFilterModel sortAndFilterModel);
    }
}
