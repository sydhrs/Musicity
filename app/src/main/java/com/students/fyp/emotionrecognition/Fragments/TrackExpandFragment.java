package com.students.fyp.emotionrecognition.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.FragmentPlaylistDetailBinding;
import com.students.fyp.emotionrecognition.databinding.FragmentTrackExpandBinding;
import com.huhx0015.hxaudio.audio.HXMusic;

public class TrackExpandFragment extends Fragment {

    FragmentTrackExpandBinding binding;
    String title, artist;
    Track track;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackExpandBinding.inflate(inflater, container, false);

        artist = getArguments().getString("artist");
        title = getArguments().getString("title");

        binding.songName.setText(title);
        binding.authorName.setText(artist);

        if(HXMusic.isPlaying()) {
            binding.playBtn.setImageResource(R.drawable.ic_pause);
        } else {
            binding.playBtn.setImageResource(R.drawable.ic_play);
        }

        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HXMusic.isPlaying()) {
                    binding.playBtn.setImageResource(R.drawable.ic_play);
                    HXMusic.pause();
                }
                else {
                    binding.playBtn.setImageResource(R.drawable.ic_pause);
                    HXMusic.resume(getContext());
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).playPreviousTrack();
                track = ((HomeActivity)getActivity()).getCurrentTrack();
                binding.songName.setText(track.getName());
                binding.authorName.setText(track.getArtist());
            }
        });

        binding.imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).playNextTrack();
                track = ((HomeActivity)getActivity()).getCurrentTrack();
                binding.songName.setText(track.getName());
                binding.authorName.setText(track.getArtist());
            }
        });

        return binding.getRoot();
    }
}