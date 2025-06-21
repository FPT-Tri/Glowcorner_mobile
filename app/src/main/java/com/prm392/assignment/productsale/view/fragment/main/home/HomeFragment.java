package com.prm392.assignment.productsale.view.fragment.main.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.FragmentHomeBinding;
import com.prm392.assignment.productsale.view.activity.MainActivity;
import com.prm392.assignment.productsale.viewmodel.fragment.main.home.HomeViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding vb;
    private NavController navController;
    private HomeViewModel viewModel;

    private int index = 0;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentHomeBinding.inflate(inflater, container, false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.Home));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.homeBottomAppBarFab.setVisibility(View.INVISIBLE);
        switch (index) {
            case 0:
                animateFab(vb.homeBarSearch, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_search, getActivity().getTheme()));
                break;
            case 1:
                animateFab(vb.homeBarCart, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.menu_icon_star, getActivity().getTheme()));
                break;
            case 2:
                animateFab(vb.homeBarFavourite, true);
                vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_favourite, getActivity().getTheme()));
                break;
        }

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        navController = Navigation.findNavController(view.findViewById(R.id.home_framgmentContainer));
        vb.homeBarSearch.setOnClickListener(button -> {
            index = 0;
            animateFab(button, false);
            navController = Navigation.findNavController(view.findViewById(R.id.home_framgmentContainer));
            navController.navigate(R.id.searchFragment, null, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_search, getActivity().getTheme()));
        });

        vb.homeBarCart.setOnClickListener(button -> {
            index = 1;
            animateFab(button, false);

            navController = Navigation.findNavController(view.findViewById(R.id.home_framgmentContainer));

            navController.navigate(R.id.cartFragment, null, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());


            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_onsale, getActivity().getTheme()));
        });

        vb.homeBarFavourite.setOnClickListener(button -> {
            index = 2;
            animateFab(button, false);
            navController = Navigation.findNavController(view.findViewById(R.id.home_framgmentContainer));
            navController.navigate(R.id.chatFragment, null, new NavOptions.Builder().setEnterAnim(R.anim.fragment_in).setExitAnim(R.anim.fragment_out).build());
            vb.homeBottomAppBarFab.setImageDrawable(getResources().getDrawable(R.drawable.navbar_icon_favourite, getActivity().getTheme()));
        });

    }

    void animateFab(View view, boolean initial) {
        view.post(() -> {

            int duration = 400;

            AnimatorSet animationSet = new AnimatorSet();

            if (initial) {
                duration = 0;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    vb.homeBottomAppBarFab.setVisibility(View.VISIBLE);

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleX", 0.0f, 1.0f);
                    animation1.setDuration(400);
                    animation1.setInterpolator(new DecelerateInterpolator());

                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleY", 0.0f, 1.0f);
                    animation2.setDuration(400);
                    animation2.setInterpolator(new DecelerateInterpolator());

                    animationSet.playTogether(animation1, animation2);
                    animationSet.start();

                }, duration);
            }

            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            float center = rect.exactCenterX();

            ObjectAnimator animation = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "X", center - (vb.homeBottomAppBarFab.getWidth() / 2f));
            animation.setDuration(duration);
            animation.setInterpolator(new DecelerateInterpolator());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleX", 1.0f, 0.75f, 1.0f);
            animation2.setDuration(duration);
            animation2.setInterpolator(new DecelerateInterpolator());

            ObjectAnimator animation3 = ObjectAnimator.ofFloat(vb.homeBottomAppBarFab, "scaleY", 1.0f, 0.75f, 1.0f);
            animation3.setDuration(duration);
            animation3.setInterpolator(new DecelerateInterpolator());

            animationSet.playTogether(animation, animation2, animation3);
            animationSet.start();

        });
    }

}