package com.students.fyp.emotionrecognition.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Adapters.AllTracksAdapter;
import com.students.fyp.emotionrecognition.Helper;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.RecyclerItemClickListener;
import com.students.fyp.emotionrecognition.databinding.FragmentMoodBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MoodFragment extends Fragment {

    FragmentMoodBinding binding;
    ArrayList<Track> tracks;
    AllTracksAdapter adapter;
    FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoodBinding.inflate(inflater, container, false);

        tracks = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvMoodSongs.setLayoutManager(linearLayoutManager);

        adapter = new AllTracksAdapter(tracks, getContext(), new AllTracksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Track item) {
                ((HomeActivity)getActivity()).startSound(item, false);
            }
        });
        binding.rvMoodSongs.setAdapter(adapter);
        database = FirebaseFirestore.getInstance();




        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        String feeling = Helper.get(getContext(),"feeling");
        if(feeling == null){
            feeling = "";
        }
        binding.title2.setText(String.format("Based on your mood (%s)",feeling));
        database
                .collection("Tracks")
                .whereEqualTo("mood",feeling)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tracks.clear();
                if(queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Track track = document.toObject(Track.class);
                        track.setKey(document.getId());
                        String isFirst = Helper.get(getContext(), "isFirst");
                        if(((HomeActivity)getActivity()).getCurrentTab() == 1 &&  isFirst != null && isFirst.contentEquals("true")){
                            ((HomeActivity)getActivity()).startSound(track, false);
                            Helper.save(getActivity(), "isFirst", "false");
                        }
                        tracks.add(track);
                    }
                    adapter.notifyDataSetChanged();
                    ((HomeActivity)getActivity()).setMoodTracks(tracks);
                    binding.moodStatus.setVisibility(View.GONE);
                } else {
                    binding.moodStatus.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}