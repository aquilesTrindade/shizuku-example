package dev.trindade.shizuku.adb_shell;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

public class ShizukuUtil {

    private static final int SHIZUKU_REQUEST_CODE = 258;

    private static final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = (requestCode, grantResult) -> {
        if (requestCode == SHIZUKU_REQUEST_CODE) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            showToast("Shizuku permission " + (granted ? "granted" : "denied"));
        }
    };

    private static final Shizuku.OnBinderReceivedListener BINDER_RECEIVED_LISTENER = () -> {
        if (Shizuku.isPreV11()) {
            showToast("PreV11 not supported");
        } else {
            showToast("Binder received");
        }
    };

    private static final Shizuku.OnBinderDeadListener BINDER_DEAD_LISTENER = () -> showToast("Binder dead");

    private static Context context;

    public static void initialize(Context context) {
        ShizukuUtil.context = context;
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER);
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER);
    }
    
    
    public static void destroy () {
        Shizuku.removeRequestPermissionResultListener(ShizukuUtil.REQUEST_PERMISSION_RESULT_LISTENER);
        Shizuku.removeBinderReceivedListener(ShizukuUtil.BINDER_RECEIVED_LISTENER);
        Shizuku.removeBinderDeadListener(ShizukuUtil.BINDER_DEAD_LISTENER);
    }
    

    public static boolean checkShizukuPermission(int code) {
        if (Shizuku.isPreV11()) {
            return false;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // If user clicked on "don't ask again"
            return false;
        } else {
            Shizuku.requestPermission(code);
            return false;
        }
    }

    public static List<String> execute(String... command) throws Exception {
        List<String> out = new ArrayList<>();

        if (Shizuku.pingBinder()) {
            // Shizuku is running
        }

        Method method = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
        method.setAccessible(true);
        ShizukuRemoteProcess process = (ShizukuRemoteProcess) method.invoke(null, new Object[]{new String[]{"sh", "-c", String.join(" ", command)}, null, "/"});
        process.waitFor();
        showToast("Process exit with code: " + process.exitValue());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.add("stdout: " + line);
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.add("stderr: " + line);
            }
        }
        return out;
    }

    private static void showToast(String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}