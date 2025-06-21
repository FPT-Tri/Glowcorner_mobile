package com.prm392.assignment.productsale.util;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.prm392.assignment.productsale.model.categories.CategoryModel;
import com.prm392.assignment.productsale.model.products.ProductSortAndFilterModel;
import com.prm392.assignment.productsale.view.fragment.dialogs.DisconnectedDialog;
import com.prm392.assignment.productsale.view.fragment.dialogs.EmailVerificationDialog;
import com.prm392.assignment.productsale.view.fragment.dialogs.LoadingDialog;
import com.prm392.assignment.productsale.view.fragment.dialogs.MessageDialog;
import com.prm392.assignment.productsale.view.fragment.dialogs.ProductSortAndFilterDialog;

import java.util.ArrayList;

public class DialogsProvider {
    private static DialogsProvider instance;
    private static FragmentManager fragmentManager;

    // App Dialogs
    private final LoadingDialog loadingDialog;
    private final DisconnectedDialog disconnectedDialog;
    private final MessageDialog messageDialog;
    private final EmailVerificationDialog emailVerificationDialog;
    private final ProductSortAndFilterDialog productSortAndFilterDialog;

    public DialogsProvider() {
        loadingDialog = new LoadingDialog();
        disconnectedDialog = new DisconnectedDialog();
        messageDialog = new MessageDialog();
        emailVerificationDialog = new EmailVerificationDialog();
        productSortAndFilterDialog = new ProductSortAndFilterDialog();
    }

    public static DialogsProvider get(Activity activity) {
        fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();

        if (instance == null) instance = new DialogsProvider();
        return instance;
    }

    public void setLoading(boolean loading) {
        if (loading && !loadingDialog.isVisible())
            loadingDialog.show(fragmentManager, loadingDialog.getTag());
        else if (loadingDialog.isVisible()) loadingDialog.dismiss();
    }

    public void setDisconnected(boolean disconnected) {
        if (disconnected) disconnectedDialog.show(fragmentManager, disconnectedDialog.getTag());
        else if (disconnectedDialog.isVisible()) disconnectedDialog.dismiss();
    }

    public void messageDialog(String title, String subTitle) {
        if (messageDialog.isVisible()) messageDialog.dismiss();
        else {
            messageDialog.setMessage(title, subTitle);
            messageDialog.show(fragmentManager, messageDialog.getTag());
        }
    }

    public void productSortAndFilterDialog(ProductSortAndFilterModel productSortAndFilterModel, ArrayList<CategoryModel> categories, ProductSortAndFilterDialog.DialogResultListener dialogResultListener) {
        if (productSortAndFilterDialog.isVisible()) productSortAndFilterDialog.dismiss();
        else {
            productSortAndFilterDialog.setSortAndFilterModel(productSortAndFilterModel);
            productSortAndFilterDialog.setDialogResultListener(dialogResultListener);
            productSortAndFilterDialog.setCategories(categories);
            productSortAndFilterDialog.show(fragmentManager, productSortAndFilterDialog.getTag());
        }
    }

}
