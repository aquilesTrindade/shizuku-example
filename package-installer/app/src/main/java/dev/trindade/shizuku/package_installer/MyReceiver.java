package dev.trindade.shizuku.package_installer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);

        switch (status) {
            case PackageInstaller.STATUS_SUCCESS:
                Log.d("MyReceiver", "Instalação bem-sucedida");
                break;
            default:
                Log.d("MyReceiver", "Falha na instalação: " + status);
                break;
        }
    }
}