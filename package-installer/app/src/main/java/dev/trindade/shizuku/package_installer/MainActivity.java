package dev.trindade.shizuku.package_installer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.pm.*;

import rikka.shizuku.*;

import dev.trindade.shizuku.package_installer.databinding.*;

import java.io.*;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private static final int SHIZUKU_REQUEST_CODE = 258;

    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = (requestCode, grantResult) -> {
        if (requestCode == SHIZUKU_REQUEST_CODE) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, "Permissão Shizuku " + (granted ? "concedida" : "negada"), Toast.LENGTH_SHORT).show();
        }
    };

    private final Shizuku.OnBinderReceivedListener BINDER_RECEIVED_LISTENER = () -> {
        if (Shizuku.isPreV11()) {
            Toast.makeText(this, "não suportado (prev11)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Binder recebido", Toast.LENGTH_LONG).show();
        }
    };

    private final Shizuku.OnBinderDeadListener BINDER_DEAD_LISTENER = () -> Toast.makeText(this, "Binder morto", Toast.LENGTH_LONG).show();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER);
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER);

        binding.requestButton.setOnClickListener(v -> {
            boolean shizukuPermissionStatus = checkShizukuPermission(SHIZUKU_REQUEST_CODE);
            Log.d(TAG, "Shizuku permission status: " + shizukuPermissionStatus);
            Toast.makeText(this, "Status da permissão Shizuku: " + shizukuPermissionStatus, Toast.LENGTH_SHORT).show();
        });

        binding.installButton.setOnClickListener(v -> {
            var apkFilePath = binding.apkPathVal.getText().toString();
            Log.d(TAG, "Install button clicked, APK path: " + apkFilePath);
            Toast.makeText(this, "Botão de instalação clicado, caminho do APK: " + apkFilePath, Toast.LENGTH_SHORT).show();
            try {
                PackageInstallerUtils.installPackage(this, apkFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Falha na instalação do pacote", Toast.LENGTH_LONG).show();
            }
        });
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