package com.students.fyp.emotionrecognition.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.students.fyp.emotionrecognition.Adapters.PageAdapter;
import com.students.fyp.emotionrecognition.Fragments.PlaylistDetailFragment;
import com.students.fyp.emotionrecognition.Fragments.SearchTracksFragment;
import com.students.fyp.emotionrecognition.Fragments.TrackExpandFragment;
import com.students.fyp.emotionrecognition.Helper;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.ActivityHomeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.huhx0015.hxaudio.audio.HXMusic;
import com.huhx0015.hxaudio.interfaces.HXMusicListener;
import com.huhx0015.hxaudio.model.HXMusicItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HXMusicListener {

    ActivityHomeBinding binding;
    Track track = null;
    boolean isPlaylistTrack = false;
    ArrayList<Track> allTracks = new ArrayList<>();
    ArrayList<Track> moodTracks = new ArrayList<>();
    ArrayList<Track> playlistTracks = new ArrayList<>();
    int currentIndex;
    int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All Songs"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Your mood"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlist"));
        binding.tabLayout.setTabGravity(binding.tabLayout.GRAVITY_FILL);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpager.setCurrentItem(tab.getPosition());
                currentTab = tab.getPosition();
                if(currentTab == 0){
                    if(allTracks.size() > 0){
                        currentIndex = 0;
                        track = allTracks.get(currentIndex);
                    }
                }
                if(currentTab == 1 && moodTracks.size() > 0){
                    currentIndex = 0;
                    track = moodTracks.get(currentIndex);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        PageAdapter pageAdapter=new PageAdapter(getSupportFragmentManager(),binding.tabLayout.getTabCount());
        binding.viewpager.setAdapter(pageAdapter);

        binding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                SearchTracksFragment fragment = new SearchTracksFragment();
                transaction.addToBackStack("");
                transaction.replace(R.id.frameLayout, fragment);
                transaction.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HXMusic.setListener(this);
        String isFirst = Helper.get(getApplicationContext(), "isFirst");
        if(isFirst != null && isFirst.contentEquals("true")){
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
            PlaylistDetailFragment myFragment = (PlaylistDetailFragment) getSupportFragmentManager().findFragmentByTag("PLAYLIST_DETAIL_FRAGMENT");
            if (myFragment != null && myFragment.isVisible()) {
                getFragmentManager().popBackStack();
                binding.frameLayout.setVisibility(View.GONE);
            }
        }
    }

    public void showSnack(String message) {
        Snackbar.make(binding.getRoot(),message, Snackbar.LENGTH_LONG).show();
    }

    public void showPlaylistDetail(String key) {
        binding.frameLayout.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        fragment.setArguments(bundle);
        transaction.addToBackStack("");
        transaction.replace(R.id.frameLayout, fragment, "PLAYLIST_DETAIL_FRAGMENT");
        transaction.commit();
    }

    public void expandMusicPlayer(View view){
        if(track != null) {
            binding.expandedFrameLayout.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TrackExpandFragment fragment = new TrackExpandFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", track.getName());
            bundle.putString("artist", track.getArtist());
            fragment.setArguments(bundle);
            transaction.addToBackStack("");
            transaction.replace(R.id.expandedFrameLayout, fragment);
            transaction.commit();
        }
    }

    public void setAllTracks(ArrayList<Track> tracks1){
        this.allTracks = tracks1;
        if(this.allTracks.size() > 0){
            this.currentIndex = 0;
        }
    }

    public void setPlaylistTracks(ArrayList<Track> tracks1){
        this.playlistTracks = tracks1;
        if(this.playlistTracks.size() > 0){
            this.currentIndex = 0;
        }
    }

    public void clearPlaylistTracks(ArrayList<Track> tracks1){
        this.playlistTracks.clear();
    }

    public void setMoodTracks(ArrayList<Track> tracks1){
        this.moodTracks = tracks1;
        if(this.moodTracks.size() > 0){
            this.currentIndex = 0;
        }
    }

    public void playTrack(View view) {
        if(HXMusic.isPlaying())
            HXMusic.pause();
        else
            HXMusic.resume(this);
    }

    public void captureEmotion(View view) {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));
    }

    public void startSound(Track track, boolean isPlaylistTrack) {
        this.isPlaylistTrack = isPlaylistTrack;
        this.track = track;
        binding.player.setVisibility(View.VISIBLE);
        binding.tvTrackName.setText(track.getName());
        binding.tvAuthorName.setText(track.getArtist());
        if(HXMusic.instance() != null) {
            if (HXMusic.isPlaying()) {
                HXMusic.stop();
            }

        }
        HXMusic.music()
                .artist(track.getArtist())
                .title(track.getName())
                .load(track.getUrl())
                .play(this);
    }

    @Override
    public void onMusicPrepared(HXMusicItem music) {
        binding.playBtn.setImageResource(R.drawable.ic_pause);
    }

    @Override
    public void onMusicCompletion(HXMusicItem music) {
        binding.playBtn.setImageResource(R.drawable.ic_play);
        playNextTrack();
    }

    @Override
    public void onMusicBufferingUpdate(HXMusicItem music, int percent) {

    }

    @Override
    public void onMusicPause(HXMusicItem music) {
        binding.playBtn.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void onMusicStop(HXMusicItem music) {
        binding.playBtn.setImageResource(R.drawable.ic_play);
    }

    public void playNextTrack(){
        if(!isPlaylistTrack){
            if(this.currentTab == 0){
                if(this.currentIndex + 1 < allTracks.size()){
                    this.currentIndex++;
                    Track track = allTracks.get(this.currentIndex);
                    startSound(track, false);
                }
            }
            else if(this.currentTab == 1){
                if(this.currentIndex + 1 < moodTracks.size()){
                    this.currentIndex++;
                    Track track = moodTracks.get(this.currentIndex);
                    startSound(track, false);
                }
            }
        }
        else{
            if(this.currentIndex + 1 < playlistTracks.size()){
                this.currentIndex++;
                Track track = playlistTracks.get(this.currentIndex);
                startSound(track, true);
            }
        }
    }

    public void playPreviousTrack(){
        if(!isPlaylistTrack){
            if(this.currentTab == 0){
                if(this.currentIndex - 1 >= 0){
                    this.currentIndex--;
                    Track track = allTracks.get(this.currentIndex);
                    startSound(track, false);
                }
            }
            else if(this.currentTab == 1){
                if(this.currentIndex - 1 >= 0){
                    this.currentIndex--;
                    Track track = moodTracks.get(this.currentIndex);
                    startSound(track, false);
                }
            }
        }
        else{
            if(this.currentIndex - 1 >= 0){
                this.currentIndex--;
                Track track = playlistTracks.get(this.currentIndex);
                startSound(track, true);
            }
        }
    }

    public void nextTrack(View view) {
        playNextTrack();
    }

    public void previousTrack(View view) {
        playPreviousTrack();
    }

    public Track getCurrentTrack(){
        return track;
    }

    public int getCurrentTab(){
        return currentTab;
    }
}