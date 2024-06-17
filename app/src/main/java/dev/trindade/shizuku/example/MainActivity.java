package dev.trindade.shizuku.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.pm.PackageManager;

import rikka.shizuku.Shizuku;

import dev.trindade.shizuku.example.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;
    private static final int SHIZUKU_REQUEST_CODE = 258;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);

        boolean shizukuPermissionStatus = checkShizukuPermission(SHIZUKU_REQUEST_CODE);
    }

    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        if (requestCode == SHIZUKU_REQUEST_CODE) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    protected void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
        super.onDestroy();
    }

    private boolean checkShizukuPermission(int code) {
        if (Shizuku.isPreV11()) {
            return false;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // if user click on "dont ask again"
            return false;
        } else {
            Shizuku.requestPermission(code);
            return false;
        }
    }
}