package com.prm392.assignment.productsale.viewmodel.fragment.main;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.prm392.assignment.productsale.data.repository.ProductsSaleRepository;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.model.cart.AddProductCartModel;
import com.prm392.assignment.productsale.model.products.ProductSaleModel;
import com.prm392.assignment.productsale.model.products.ProductSalePageResponseModel;
import com.prm392.assignment.productsale.model.products.StoreLocation;
import com.prm392.assignment.productsale.util.UserAccountManager;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

public class ProductPageViewModel extends ViewModel {
    private final ProductsSaleRepository productsSaleRepository;

    private long productId;
    private final String token;

    @Getter
    @Setter
    private ProductSaleModel productSaleModel;

    @Getter
    @Setter
    private StoreLocation storeLocation;

    @Getter
    @Setter
    private int productQuantity = 1;
    @Getter
    @Setter
    private UserModel userModel;

    public ProductPageViewModel(@NotNull Application application) {
        super();
        productsSaleRepository = new ProductsSaleRepository();

        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
        userModel = UserAccountManager.getUser(application);
    }


    public static final ViewModelInitializer<ProductPageViewModel> initializer = new ViewModelInitializer<>(
            ProductPageViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new ProductPageViewModel(app);
            }
    );

    public LiveData<Response<ProductSalePageResponseModel>> getProductSale() {
        return productsSaleRepository.getProductSale(token, productId);
    }

    public LiveData<Response<BaseResponseModel>> addProductToCart() {
        AddProductCartModel addProductCartModel = new AddProductCartModel(userModel.getId(),productId, productQuantity);
        return productsSaleRepository.addProductToCart(token, addProductCartModel);
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }


}
