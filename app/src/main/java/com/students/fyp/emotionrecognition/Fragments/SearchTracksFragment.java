package com.students.fyp.emotionrecognition.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Adapters.AllTracksAdapter;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.RecyclerItemClickListener;
import com.students.fyp.emotionrecognition.databinding.FragmentAllTracksBinding;
import com.students.fyp.emotionrecognition.databinding.FragmentSearchTracksBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchTracksFragment extends Fragment {


    protected FirebaseFirestore rootRef;
    ArrayList<Track> tracks;
    FragmentSearchTracksBinding binding;
    AllTracksAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchTracksBinding.inflate(inflater, container, false);

        rootRef = FirebaseFirestore.getInstance();

        tracks = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvAllTracks.setLayoutManager(linearLayoutManager);
        adapter = new AllTracksAdapter(tracks, getActivity(), new AllTracksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Track item) {
                ((HomeActivity)getActivity()).startSound(item, false);
                getFragmentManager().popBackStack();
            }
        });
        binding.rvAllTracks.setAdapter(adapter);
        getAllTracks();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });



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
                        }
                    }
                });
    }

    void filter(String text){
        ArrayList<Track> temp = new ArrayList();
        for(Track d: tracks){
            if(d.getName().trim().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }
}