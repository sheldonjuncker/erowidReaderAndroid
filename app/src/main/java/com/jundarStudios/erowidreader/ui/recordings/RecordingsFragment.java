package com.jundarStudios.erowidreader.ui.recordings;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jundarStudios.erowidreader.AudioRecordTestActivity;
import com.jundarStudios.erowidreader.R;

public class RecordingsFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "com.jundarStudios.erowidreader.MESSAGE";
    private RecordingsViewModel mViewModel;

    public static RecordingsFragment newInstance() {
        return new RecordingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recordings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecordingsViewModel.class);
        // TODO: Use the ViewModel
    }
}