package com.students.fyp.emotionrecognition.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.students.fyp.emotionrecognition.Adapters.PlaylistTracksAdapter;
import com.students.fyp.emotionrecognition.Models.Playlist;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.databinding.FragmentAllTracksBinding;
import com.students.fyp.emotionrecognition.databinding.FragmentPlaylistDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PlaylistDetailFragment extends Fragment {


    protected FirebaseFirestore rootRef;
    ArrayList<Track> tracks;
    FragmentPlaylistDetailBinding binding;
    PlaylistTracksAdapter adapter;
    String playlistId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false);

        playlistId = getArguments().getString("key");

        rootRef.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Playlists")
                .document(playlistId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Playlist playlist = documentSnapshot.toObject(Playlist.class);
                binding.playlistName.setText(playlist.getName());
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();

            }
        });

        tracks = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvAllTracks.setLayoutManager(linearLayoutManager);
        adapter = new PlaylistTracksAdapter(tracks,playlistId, getActivity());
        binding.rvAllTracks.setAdapter(adapter);

        getAllTracks();

        return binding.getRoot();
    }

    public void getAllTracks() {
        rootRef.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Playlists")
                .document(playlistId)
                .collection("Tracks")
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
                            binding.totalTracks.setText(String.format("%d tracks", tracks.size()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }
}