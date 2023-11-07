package com.example.projectprm392.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm392.MyApplication;
import com.example.projectprm392.R;
import com.example.projectprm392.activity.MainActivity;
import com.example.projectprm392.activity.PlayMusicActivity;
import com.example.projectprm392.adapter.SongAdapter;
import com.example.projectprm392.constant.Constant;
import com.example.projectprm392.constant.GlobalFuntion;
import com.example.projectprm392.databinding.FragmentPopularSongsBinding;
import com.example.projectprm392.model.Song;
import com.example.projectprm392.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopularSongsFragment extends Fragment {

    private FragmentPopularSongsBinding mFragmentPopularSongsBinding;
    private List<Song> mListSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPopularSongsBinding = FragmentPopularSongsBinding.inflate(inflater, container, false);

        getListPopularSongs();
        initListener();

        return mFragmentPopularSongsBinding.getRoot();
    }

    private void getListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    if (song.getCount() > 0) {
                        mListSong.add(song);
                    }
                }
                Collections.sort(mListSong, (song1, song2) -> song2.getCount() - song1.getCount());
                displayListPopularSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentPopularSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentPopularSongsBinding.rcvData.setAdapter(songAdapter);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    private void initListener() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null || activity.getActivityMainBinding() == null) {
            return;
        }
        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
    }
}
