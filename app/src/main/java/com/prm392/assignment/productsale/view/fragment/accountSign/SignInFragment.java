package com.prm392.assignment.productsale.view.fragment.accountSign;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.FragmentSignInBinding;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.util.DialogsProvider;
import com.prm392.assignment.productsale.util.SharedPrefManager;
import com.prm392.assignment.productsale.util.UserAccountManager;
import com.prm392.assignment.productsale.view.activity.AccountSign;
import com.prm392.assignment.productsale.view.activity.MainActivity;
import com.prm392.assignment.productsale.viewmodel.fragment.accountSign.SignInViewModel;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding vb;
    private SignInViewModel viewModel;
    private NavController navController;

    public SignInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (vb == null)
            vb = FragmentSignInBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (((AccountSign) getActivity()).isBackButtonVisible()) {
            ((AccountSign) getActivity()).setTitle(getString(R.string.Sign_In));
            ((AccountSign) getActivity()).setBackButton(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel != null) return;

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        vb.signInButton.setOnClickListener(button -> {
            if (isDataValid()) signIn();
        });

        vb.signInSignUp.setOnClickListener(button -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });

    }

    boolean isDataValid() {
        boolean validData = true;

        if (vb.signInPassword.getError() != null || vb.signInPassword.getEditText().getText().length() == 0) {
            vb.signInPassword.requestFocus();
            vb.signInPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.signInEmail.getError() != null || vb.signInEmail.getEditText().getText().length() == 0) {
            vb.signInEmail.requestFocus();
            vb.signInEmail.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void signIn() {
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.signIn(vb.signInEmail.getEditText().getText().toString(), vb.signInPassword.getEditText().getText().toString())
                .observe(getViewLifecycleOwner(), response -> {

                    DialogsProvider.get(getActivity()).setLoading(false);

                    switch (response.code()) {
                        case BaseResponseModel.SUCCESSFUL_OPERATION:
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra(MainActivity.JUST_SIGNED_IN, true);

                            SharedPrefManager.get(getContext()).setRememberMe(vb.signInRememberMe.isChecked());
                            UserAccountManager.signIn(getActivity(), intent, response.body().getToken(), response.body().getUser());
                            break;

                        case BaseResponseModel.FAILED_AUTH:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Login_failed), getString(R.string.Invalid_username_or_password));
                            break;

                        case BaseResponseModel.FAILED_REQUEST_FAILURE:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Request_Error), getString(R.string.Please_Check_your_connection));
                            break;

                        default:
                            DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error), getString(R.string.Code) + response.code());
                    }

                });

    }

}