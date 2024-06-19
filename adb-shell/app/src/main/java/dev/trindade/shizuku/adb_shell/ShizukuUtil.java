package dev.trindade.shizuku.adb_shell;

import androidx.appcompat.app.*;
import android.os.*;
import android.widget.*;
import android.content.pm.*;
import android.util.*;
import rikka.shizuku.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class ShizukuUtil {

    private static final int SHIZUKU_REQUEST_CODE = 258;

    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = (requestCode, grantResult) -> {
        if (requestCode == SHIZUKU_REQUEST_CODE) {
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, "Shizuku permission " + (granted ? "granted " : "denied "), Toast.LENGTH_SHORT).show();
        }
    };

    private final Shizuku.OnBinderReceivedListener BINDER_RECEIVED_LISTENER = () -> {
        if (Shizuku.isPreV11()) {
            Toast.makeText(this, "PreV11 not supported", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Binder received" , Toast.LENGTH_LONG).show();
        }
    };

    private final Shizuku.OnBinderDeadListener BINDER_DEAD_LISTENER = () -> Toast.makeText(this, "Binder morto", Toast.LENGTH_LONG).show();

    public static boolean checkShizukuPermission(int code) {
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
    
    public static void binders () {
         Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
         Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER);
         Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER);
    }
    
    public static List<String> execute (String... command) throws Exception {
         List<String> out = new ArrayList<>();
         
         if (Shizuku.pingBinder()) {
             //Shizuku is running
         }
         
         Method mtd = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
         mtd.setAccessible(true);
         ShizukuRemoteProcess prcss = (ShizukuRemoteProcess) mtd.invoke(null, new Object[]{new String[]{"sh", "-c", String.join(" ", command)}, null, "/"});
         prcss.waitFor();
         Toast.makeText(this, "Process exit wit code: " + prcss.exitValue(), 4000).show();
         
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(prcss.getInputStream()))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 out.add("tdevS: " + line);
             }
         }
         
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(prcss.getErrorStream()))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 out.add("tdevE: " + line);
             }
         }
         return out;
    }
}