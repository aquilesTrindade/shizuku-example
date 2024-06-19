package dev.trindade.shizuku.adb_shell;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import dev.trindade.shizuku.adb_shell.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int SHIZUKU_REQUEST_CODE = 258;
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ShizukuUtil.initialize(this);
        ShizukuUtil.binders();

        binding.requestButton.setOnClickListener(v -> {
            boolean shizukuPermissionStatus = ShizukuUtil.checkShizukuPermission(SHIZUKU_REQUEST_CODE);
            Log.d(TAG, "Shizuku permission status: " + shizukuPermissionStatus);
            Toast.makeText(this, "Shizuku permission status: " + shizukuPermissionStatus, Toast.LENGTH_SHORT).show();
        });

        binding.installButton.setOnClickListener(v -> {
            Toast.makeText(this, "wait...", Toast.LENGTH_SHORT).show();
            runOnUiThread(() -> {
                try {
                    List<String> output = ShizukuUtil.execute(binding.shellCommand.getText().toString());
                    StringBuilder outputText = new StringBuilder();
                    for (String line : output) {
                        outputText.append(line).append("\n");
                    }
                    TextView outputTextView = new TextView(this);
                    outputTextView.setText(Html.fromHtml(OutputHelper.formatError(outputText.toString()), Html.FROM_HTML_MODE_LEGACY));
                    binding.scroll.addView(outputTextView);
                    outputTextView.setTextIsSelectable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Package installation failed", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        ShizukuUtil.destroy();
        super.onDestroy();
    }
}