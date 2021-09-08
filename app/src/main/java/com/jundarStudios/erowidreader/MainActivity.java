package com.jundarStudios.erowidreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jundarStudios.erowidreader.audioRecording.AudioRecorder;
import com.jundarStudios.erowidreader.audioRecording.AudioUtils;
import com.jundarStudios.erowidreader.audioRecording.RawAudioRecorder;
import com.jundarStudios.erowidreader.databinding.ActivityMainBinding;
import com.jundarStudios.erowidreader.experience.ExperienceReport;
import com.jundarStudios.erowidreader.experience.ExperienceRepository;
import com.jundarStudios.erowidreader.experience.ExperienceResultsCallback;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    ExecutorService executorService;
    Handler mainThreadHandler;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            //Actually need to display a message about why this is necessary.
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_recordings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //We'll need some threads for recording audio and waiting on transcription results and text to speech results
        executorService = Executors.newFixedThreadPool(4);
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        //Because this is the only thing the app does, we just request it immediately.
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    public void startRecording(View view) {
        System.out.println("starting recording...");

        ExperienceResultsCallback resultsCallback = new ExperienceResultsCallback() {
            @Override
            public void success(ArrayList<ExperienceReport> experienceReports) {
                binding.resultText.setText("success!");
            }

            @Override
            public void error(int code, String error) {
                binding.resultText.setText("Error: " + error);
            }
        };

        executorService.execute(() -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            RawAudioRecorder audioRecorder = new RawAudioRecorder(MediaRecorder.AudioSource.MIC, AudioRecorder.DEFAULT_SAMPLE_RATE);
            audioRecorder.start();
            while(!audioRecorder.isPausing()) {

            }
            audioRecorder.stop();
            byte[] audioData = audioRecorder.getCompleteRecordingAsWav();
            System.out.println("recorded some audio: " + audioData.length + " bytes");

            //Let's upload it to our server and get some results!
            ExperienceRepository repository = new ExperienceRepository(audioData);
            mainThreadHandler.post(() -> {
                repository.loadExperienceReports(resultsCallback);
            });
        });
        System.out.println("thread is off and running!");
    }
}