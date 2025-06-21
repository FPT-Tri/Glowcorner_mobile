package com.prm392.assignment.productsale.viewmodel.fragment.accountSign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.assignment.productsale.data.repository.AuthRepository;
import com.prm392.assignment.productsale.model.SignInModel;
import com.prm392.assignment.productsale.model.UserResponseModel;

import retrofit2.Response;

public class SignInViewModel extends ViewModel {
    private final AuthRepository authRepository;

    public SignInViewModel() {
        super();
        authRepository = new AuthRepository();
    }

    public LiveData<Response<UserResponseModel>> signIn(String username, String password) {
        SignInModel signInModel = new SignInModel();
        signInModel.setUsername(username);
        signInModel.setPassword(password);

        return authRepository.signIn(signInModel);
    }
}
