package com.prm392.assignment.productsale.viewmodel.fragment.accountSign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.assignment.productsale.data.repository.AuthRepository;
import com.prm392.assignment.productsale.model.SignUpModel;
import com.prm392.assignment.productsale.model.UserResponseModel;

import retrofit2.Response;

public class SignUpViewModel extends ViewModel {
    private final AuthRepository authRepository;

    public SignUpViewModel() {
        super();
        authRepository = new AuthRepository();
    }

    public LiveData<Response<UserResponseModel>> signUp(String username, String email, String password, String phoneNumber, String address) {
        SignUpModel signUpModel = new SignUpModel();
        signUpModel.setUsername(username);
        signUpModel.setEmail(email);
        signUpModel.setPassword(password);
        signUpModel.setPhoneNumber(phoneNumber);
        if (address != null && address.isEmpty()) address = null;
        signUpModel.setAddress(address);

        return authRepository.signUp(signUpModel);
    }

}
