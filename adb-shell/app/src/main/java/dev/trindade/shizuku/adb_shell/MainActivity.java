package dev.trindade.shizuku.adb_shell;

import androidx.appcompat.app.*;
import android.os.*;
import android.widget.*;
import android.content.pm.*;
import android.util.*;
import android.text.*;
import android.text.style.*;

import rikka.shizuku.*;

import dev.trindade.shizuku.adb_shell.databinding.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ShizukuUtil.initialize(this);
        ShizukuUtil.binders();

        binding.requestButton.setOnClickListener(v -> {
            boolean shizukuPermissionStatus = checkShizukuPermission(SHIZUKU_REQUEST_CODE);
            Log.d(TAG, "Shizuku permission status: " + shizukuPermissionStatus);
            Toast.makeText(this, "Shizuku permission status:  " + shizukuPermissionStatus, Toast.LENGTH_SHORT).show();
        });

        binding.installButton.setOnClickListener(v -> {
            Toast.makeText(this, "wait...", Toast.LENGTH_SHORT).show();
            runOnUiThread(()->{
                  try {
                     List<String> output = ShizukuUtil.execute(binding.shellCommand.getText().toString());
                     StringBuilder outputText = new StringBuilder();
                     for (String line : output) {
                         outputText.append(line).append("\n");
                     }
                     TextView outputTextView = new TextView(this);
                     outputTextView.setText(ShizukuUtil.formatOutput(outputText));
                     binding.scroll.addView(outputTextView);
                     outputTextView.setTextIsSelectable(true);
                  } catch (Exception e) {
                     e.printStackTrace();
                     Toast.makeText(this, "Package installation failed ", Toast.LENGTH_LONG).show();
                  }
            });
        });
    }

    @Override
    protected void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
        super.onDestroy();
    }
}