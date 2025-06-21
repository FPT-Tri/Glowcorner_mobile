package com.prm392.assignment.productsale.view.fragment.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.databinding.FragmentProductPageBinding;
import com.prm392.assignment.productsale.model.BaseResponseModel;
import com.prm392.assignment.productsale.model.products.ProductSaleModel;
import com.prm392.assignment.productsale.model.products.StoreLocation;
import com.prm392.assignment.productsale.util.AppSettingsManager;
import com.prm392.assignment.productsale.util.DialogsProvider;
import com.prm392.assignment.productsale.view.activity.MainActivity;
import com.prm392.assignment.productsale.viewmodel.fragment.main.ProductPageViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lecho.lib.hellocharts.view.LineChartView;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding vb;
    private ProductPageViewModel viewModel;
    private NavController navController;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private GoogleMap googleMap;


    public ProductPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestNotificationPermission();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentProductPageBinding.inflate(inflater, container, false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.Product));
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(ProductPageViewModel.initializer))
                .get(ProductPageViewModel.class);
        if (getArguments() != null) viewModel.setProductId(getArguments().getLong("productId"));

        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.product_page_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(map -> {
                this.googleMap = map;

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            });
        }

        vb.increaseBtn.setOnClickListener(button -> {
            viewModel.setProductQuantity(viewModel.getProductQuantity() + 1);
            vb.txtQuantity.setText(viewModel.getProductQuantity() + "");
        });

        vb.decreaseBtn.setOnClickListener(button -> {
            if (viewModel.getProductQuantity() > 1) {
                viewModel.setProductQuantity(viewModel.getProductQuantity() - 1);
                vb.txtQuantity.setText(viewModel.getProductQuantity() + "");
            }
        });

        vb.txtQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int quantity = Integer.parseInt(vb.txtQuantity.getText().toString());
                    if (quantity < 1) {
                        viewModel.setProductQuantity(1);
                        vb.txtQuantity.setText("1");
                    } else viewModel.setProductQuantity(quantity);
                } catch (Exception e) {
                    viewModel.setProductQuantity(1);
                    vb.txtQuantity.setText("1");
                }
            }
        });

        vb.productPageBack.setOnClickListener(button -> {
            getActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        //Add to cart
        vb.addToCartBtn.setOnClickListener(button -> {
            vb.productPageLoadingPage.setVisibility(View.VISIBLE);
            viewModel.addProductToCart().observe(getViewLifecycleOwner(), response -> {
                vb.productPageLoadingPage.setVisibility(View.GONE);
                viewModel.setProductQuantity(1);
                vb.txtQuantity.setText("1");
                DialogsProvider.get(getActivity()).messageDialog("Success","Add to Cart Successful");
                sendCartNotification(requireContext());
                vb.productPageLoadingPage.setVisibility(View.GONE);
            });
        });

        vb.productPageNavigateButton.setOnClickListener(button -> {
            Uri uri = Uri.parse("google.navigation:q=" + viewModel.getStoreLocation().getLatitude() + "," + viewModel.getStoreLocation().getLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        });

        loadProductSale();
    }
    // Hàm gửi thông báo
    private void sendCartNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        String channelId = "cart_notifications";
        String channelName = "Cart Notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("Giỏ hàng")
                .setContentText("Bạn vừa thêm một sản phẩm vào giỏ hàng!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }


    void loadProductSale() {
        vb.productPageLoadingPage.setVisibility(View.VISIBLE);

        viewModel.getProductSale().observe(getViewLifecycleOwner(), response -> {

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    if (response.body() != null) {
                        viewModel.setProductSaleModel(response.body().getProduct());
                        viewModel.setStoreLocation(response.body().getStoreLocation());
                        renderProductSaleData();
                        vb.productPageLoadingPage.setVisibility(View.GONE);
                        vb.getRoot().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.lay_on));
                    }
                    break;

                case BaseResponseModel.FAILED_NOT_FOUND:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Product_Not_Found), getString(R.string.Product_Not_Found_in_Server));
                    break;

                case BaseResponseModel.FAILED_REQUEST_FAILURE:
                    Toast.makeText(getContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getContext(), "Server Error | Code: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void renderProductSaleData() {
        ProductSaleModel productSaleModel = viewModel.getProductSaleModel();
        StoreLocation storeLocation = viewModel.getStoreLocation();
        vb.productPageBrand.setText(productSaleModel.getCategoryName());
        Double productPrice = Double.parseDouble(String.format("%.2f", productSaleModel.getPrice()));
        vb.productPagePrice.setText(productPrice + "$");
        vb.txtQuantity.setText(viewModel.getProductQuantity() + "");

        Glide.with(this)
                .load(Uri.parse(productSaleModel.getProductImage()))
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(vb.productPageImage);

        vb.productPageBrand.setText(productSaleModel.getCategoryName());
        vb.productPageTitle.setText(productSaleModel.getProductName());
        vb.productPageDescription.setText(productSaleModel.getBriefDescription());
        vb.productSaleFullDescription.setText(productSaleModel.getFullDescription());
        vb.productSaleTechSpecsText.setText(productSaleModel.getTechnicalSpecifications());
        addProductOnMap(storeLocation.getLatitude(), storeLocation.getLongitude(), storeLocation.getAddress());

        String fullDescription = productSaleModel.getFullDescription();

        if (fullDescription.length() > 120) {
            String shortDescription = fullDescription.substring(0, 110) + "... ";

            SpannableString readMore = new SpannableString(getString(R.string.Read_More));
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {

                    vb.productSaleFullDescription.animate().alpha(0).setDuration(250).withEndAction(() -> {
                        vb.productSaleFullDescription.setText(fullDescription);
                        vb.productSaleFullDescription.animate().alpha(1f).setDuration(250).start();
                    }).start();

                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            readMore.setSpan(clickableSpan, 0, readMore.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            vb.productSaleFullDescription.setText(shortDescription);
            vb.productSaleFullDescription.append(readMore);
            vb.productSaleFullDescription.setMovementMethod(LinkMovementMethod.getInstance());

        } else vb.productSaleFullDescription.setText(fullDescription);

    }

    private void addProductOnMap(double lat, double lng, String storeName) {
        try {
            LatLng productLocation = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(productLocation)
                    .title(storeName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(productLocation)
                    .zoom(googleMap.getCameraPosition().zoom < 8 ? 8 : googleMap.getCameraPosition().zoom)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Map Marker Error", Toast.LENGTH_SHORT).show();
        }
    }

    public String dateTimeConvert(String dateTime) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        String outputPattern = "dd/MM/yyyy";

        try {
            SimpleDateFormat input = new SimpleDateFormat(inputPattern);
            input.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date parsed = input.parse(dateTime);

            SimpleDateFormat destFormat = new SimpleDateFormat(outputPattern);
            destFormat.setTimeZone(TimeZone.getDefault());

            return destFormat.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }

}