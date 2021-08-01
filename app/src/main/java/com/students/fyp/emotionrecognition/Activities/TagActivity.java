package com.students.fyp.emotionrecognition.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.students.fyp.emotionrecognition.databinding.ActivityTagBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

public class TagActivity extends AppCompatActivity {

    ActivityTagBinding binding;
    String playlist;
    String trackId;
    FirebaseFirestore database;
    KProgressHUD progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progress = KProgressHUD.create(TagActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("Updating...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        String trackName = getIntent().getStringExtra("name");
        String trackArtist = getIntent().getStringExtra("artist");
        String trackMood = getIntent().getStringExtra("mood");
        playlist = getIntent().getStringExtra("playlist");
        trackId = getIntent().getStringExtra("trackId");

        database = FirebaseFirestore.getInstance();

        binding.trackName.setText(trackName);
        binding.artistName.setText(trackArtist);
        binding.emotion.setText(String.format("Current Tag: %s", trackMood));
    }

    public void changeEmotion(View view) {
        TextView textView = (TextView) view;
        progress.show();
        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Playlists")
                .document(playlist)
                .collection("Tracks")
                .document(trackId)
                .update("mood", textView.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress.dismiss();
                finish();
            }
        });
    }
}
