package com.students.fyp.emotionrecognition.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Adapters.AllTracksAdapter;
import com.students.fyp.emotionrecognition.Models.Playlist;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.RecyclerItemClickListener;
import com.students.fyp.emotionrecognition.databinding.FragmentAllTracksBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllTracksFragment extends Fragment {


    protected FirebaseFirestore rootRef;
    ArrayList<Track> tracks;
    FragmentAllTracksBinding binding;
    AllTracksAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllTracksBinding.inflate(inflater, container, false);

        rootRef = FirebaseFirestore.getInstance();

        tracks = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvAllTracks.setLayoutManager(linearLayoutManager);
        adapter = new AllTracksAdapter(tracks, getActivity(), new AllTracksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Track item) {
                ((HomeActivity)getActivity()).startSound(item, false);
            }
        });
        binding.rvAllTracks.setAdapter(adapter);
        getAllTracks();




        return binding.getRoot();
    }

    public void getAllTracks() {
        rootRef.collection("Tracks")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        tracks.clear();
                        if(value.getDocuments().size() > 0) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Track track = document.toObject(Track.class);
                                track.setKey(document.getId());
                                tracks.add(track);
                            }
                            adapter.notifyDataSetChanged();
                            ((HomeActivity)getActivity()).setAllTracks(tracks);
                        }
                    }
                });
    }
}