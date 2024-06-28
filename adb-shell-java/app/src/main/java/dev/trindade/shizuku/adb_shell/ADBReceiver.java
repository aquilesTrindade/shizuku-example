package dev.trindade.shizuku.adb_shell;

import android.widget.*;
import android.view.*;
import android.content.*;
import android.content.pm.*;
import android.util.*;

public class ADBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);

        switch (status) {
            case PackageInstaller.STATUS_SUCCESS:
                Toast.makeText(context, "ADBReceiver Installation successful", 4000).show();
                break;
            default:
                Toast.makeText(context, "ADBReceiver Installation failed" + status, 4000).show();
                break;
        }
    }
}