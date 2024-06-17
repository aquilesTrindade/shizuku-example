package dev.trindade.shizuku.package_installer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.pm.PackageManager;
import rikka.shizuku.Shizuku;
import dev.trindade.shizuku.package_installer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int SHIZUKU_REQUEST_CODE = 258;

    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = (requestCode, grantResult) -> {
        if (requestCode == SHIZUKU_REQUEST_CODE) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, "Shizuku permission " + (granted ? "granted" : "denied"), Toast.LENGTH_SHORT).show();
        }
    };

    private final Shizuku.OnBinderReceivedListener BINDER_RECEIVED_LISTENER = () -> {
        if (Shizuku.isPreV11()) {
            Toast.makeText(this, "not supported (prev11)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Binder received", Toast.LENGTH_LONG).show();
        }
    };

    private final Shizuku.OnBinderDeadListener BINDER_DEAD_LISTENER = () -> Toast.makeText(this, "Binder dead", Toast.LENGTH_LONG).show();

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
        });

        binding.installButton.setOnClickListener(v -> {
            var apkFilePath = binding.apkPathVal.getText().toString();
            try {
                PackageInstallerUtils.installPackage(this, apkFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to install package", Toast.LENGTH_LONG).show();
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