package com.example.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Adapter.QuizAdapter;
import com.example.mobile.Api.ApiClient;
import com.example.mobile.Api.ApiService;
import com.example.mobile.Models.QuizResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {
    private RecyclerView quizRecyclerView;
    private ProgressBar progressBar;
    private TextView errorText;
    private QuizAdapter quizAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Binding UI components
        quizRecyclerView = findViewById(R.id.quizRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);

        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationManager.setupNavigation(this, bottomNavigationView);

        // Load quiz data
        loadQuizzes();
    }

    private void loadQuizzes() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<QuizResponse> call = apiService.getQuizzes();

        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    quizAdapter = new QuizAdapter(QuizActivity.this, response.body().getData());
                    quizRecyclerView.setAdapter(quizAdapter);
                } else {
                    errorText.setText("Không thể tải câu hỏi khảo sát.");
                    errorText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                errorText.setText("Lỗi kết nối: " + t.getMessage());
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }
}
