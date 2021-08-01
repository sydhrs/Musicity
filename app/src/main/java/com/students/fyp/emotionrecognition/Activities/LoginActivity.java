package com.students.fyp.emotionrecognition.Activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

public class LoginActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_sign_in);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel(getString(R.string.logging_in))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);



        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
    }

    public void loginUserAccount(View view) {

        String email, password;
        email = binding.edtUsernameSignin.getText().toString();
        password = binding.edtUserPasswordSignin.getText().toString();

        if (TextUtils.isEmpty(email)) {
            binding.edtUsernameSignin.setError(getString(R.string.email_required));
            showSnack(getString(R.string.email_required));
            return;
        }
        if (!isValidEmail(email)) {
            binding.edtUsernameSignin.setError(getString(R.string.invalid_email_address));
            showSnack(getString(R.string.invalid_email_address));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.edtUserPasswordSignin.setError(getString(R.string.password_required));
            showSnack(getString(R.string.password_required));
            return;
        }

        if (password.length() < 6) {
            binding.edtUserPasswordSignin.setError(getString(R.string.minimum_characters_password));
            showSnack(getString(R.string.password_length));
            return;
        }

        dialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finishAffinity();
                        } else {
                            showSnack(task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    void showSnack(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}