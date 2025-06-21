package com.prm392.assignment.productsale.viewmodel.fragment.main.home;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.ProductsResponseModel;
import com.prm392.assignment.productsale.util.UserAccountManager;
import retrofit2.Response;

public class FavViewModel extends ViewModel {
    private LiveData<Response<ProductsResponseModel>> products;
    private final String token;

    public FavViewModel(@NonNull Application application) {
        super();
        token = UserAccountManager.getToken(application,UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public static final ViewModelInitializer<FavViewModel> initializer = new ViewModelInitializer<>(
            FavViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new FavViewModel(app);
            }
    );

//    public LiveData<Response<ProductsResponseModel>> getFavoriteProducts(){
//        products = repository.getFavoriteProducts(token);
//        return products;
//    }
//
//    public void removeObserverOfProducts(LifecycleOwner lifecycleOwner){
//        products.removeObservers(lifecycleOwner);
//    }
//
//    public LiveData<Response<BaseResponseModel>> removeFavourite(long productId){
//        return repository.removeFavourite(token,productId);
//    }
}
