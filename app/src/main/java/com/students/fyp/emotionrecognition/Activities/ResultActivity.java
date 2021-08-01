package com.students.fyp.emotionrecognition.Activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.students.fyp.emotionrecognition.Helper;
import com.students.fyp.emotionrecognition.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final String feeling = getIntent().getStringExtra("feeling");

        binding.selectedImage.setImageBitmap(CameraActivity.bitmap);
        binding.feeling.setText(feeling);

        binding.retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, CameraActivity.class));
                finish();
            }
        });

        binding.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.save(ResultActivity.this, "feeling", feeling);
                Helper.save(ResultActivity.this, "isFirst", "true");
                finish();
            }
        });
    }

}
