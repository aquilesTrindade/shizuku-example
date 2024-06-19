package dev.trindade.shizuku.adb_shell;

import android.content.*;
import android.content.pm.*;
import android.util.*;

public class ADBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);

        switch (status) {
            case PackageInstaller.STATUS_SUCCESS:
                Log.d("ADBReceiver", "Installation successful");
                break;
            default:
                Log.d("ADBReceiver", "Installation failed: "  + status);
                break;
        }
    }
}