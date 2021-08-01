package com.students.fyp.emotionrecognition.Activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import com.students.fyp.emotionrecognition.Models.User;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

public class SignupActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel(getString(R.string.creating_new_account))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);



        auth=FirebaseAuth.getInstance();

        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username,password,email;
                password=binding.edtUserPassword.getText().toString();
                email=binding.edtUseremail.getText().toString();
                username=binding.edtUsername.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    binding.edtUsername.setError(getString(R.string.required));
                    showSnack(getString(R.string.username));
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    binding.edtUseremail.setError(getString(R.string.email_required));
                    showSnack(getString(R.string.email_required));
                    return;
                }
                if (!isValidEmail(email)) {
                    binding.edtUseremail.setError(getString(R.string.invalid_email_address));
                    showSnack(getString(R.string.invalid_email_address));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    binding.edtUserPassword.setError(getString(R.string.password_required));
                    showSnack(getString(R.string.password_required));
                    return;
                }

                if (password.length() < 6) {
                    binding.edtUserPassword.setError(getString(R.string.minimum_characters_password));
                    showSnack(getString(R.string.password_length));
                    return;
                }

                dialog.show();

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(username, email, password);
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            database.collection("Users")
                                    .document(task.getResult().getUser().getUid())
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    dialog.dismiss();
                                    showSnack(getString(R.string.user_created_successfully));
                                    binding.signupGroup.setVisibility(View.GONE);
                                    binding.statusSignup.setText(getString(R.string.logging_in));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                            finishAffinity();
                                        }
                                    }, 2000);
                                }
                            });
                        }
                        else {
                            dialog.dismiss();
                            showSnack(task.getException().getLocalizedMessage());
                        }
                    }
                });
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