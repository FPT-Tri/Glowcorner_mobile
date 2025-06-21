package com.prm392.assignment.productsale.viewmodel.fragment.main.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.prm392.assignment.productsale.data.repository.CategoryRepository;
import com.prm392.assignment.productsale.data.repository.ProductsSaleRepository;
import com.prm392.assignment.productsale.model.categories.CategoriesResponseModel;
import com.prm392.assignment.productsale.model.products.ProductSortAndFilterModel;
import com.prm392.assignment.productsale.model.products.ProductsSaleResponseModel;
import com.prm392.assignment.productsale.util.UserAccountManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    public static final ViewModelInitializer<SearchViewModel> initializer = new ViewModelInitializer<>(
            SearchViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new SearchViewModel(app);
            }
    );
    private final ProductsSaleRepository productsRepository;
    private final CategoryRepository categoryRepository;
    private final String token;
    @Getter
    @Setter
    private ProductSortAndFilterModel productSortAndFilterModel;

    @Getter
    @Setter
    private String searchStr;
    private LiveData<Response<ProductsSaleResponseModel>> products;

    public SearchViewModel(@NotNull Application application) {
        super();

        productsRepository = new ProductsSaleRepository();
        categoryRepository = new CategoryRepository();
        productSortAndFilterModel = new ProductSortAndFilterModel();
        productSortAndFilterModel.setPageIndex(1);
        productSortAndFilterModel.setPageSize(8);
        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<ProductsSaleResponseModel>> getProducts() {
        List<Integer> categoryIds = new ArrayList<>(productSortAndFilterModel.getCategories());
        return productsRepository.getProducts(token, productSortAndFilterModel.getPageIndex(), productSortAndFilterModel.getPageSize(), searchStr, productSortAndFilterModel.getSortBy(), productSortAndFilterModel.getSortDescending(), productSortAndFilterModel.getMinPrice(), productSortAndFilterModel.getMaxPrice(), categoryIds.isEmpty() ? null : categoryIds);
    }

    public void removeObserverProducts(LifecycleOwner lifecycleOwner) {
        products.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<CategoriesResponseModel>> getCategories() {
        return categoryRepository.getCategories();
    }

}
