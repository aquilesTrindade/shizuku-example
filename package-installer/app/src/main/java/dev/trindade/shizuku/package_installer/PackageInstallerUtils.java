package dev.trindade.shizuku.package_installer;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.//Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PackageInstallerUtils {

    private static final String TAG = "PackageInstallerUtils";

    public static void installPackage(Context context, String apkFilePath) throws IOException {
        //Log.d(TAG, "Starting package installation for: " + apkFilePath);
        Toast.makeText(context, "Iniciando instalação do pacote", Toast.LENGTH_SHORT).show();

        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);

        int sessionId = packageInstaller.createSession(params);
        //Log.d(TAG, "Created session with ID: " + sessionId);
        Toast.makeText(context, "Sessão criada com ID: " + sessionId, Toast.LENGTH_SHORT).show();
        
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        InputStream in = null;
        OutputStream out = null;
        try {
            File apkFile = new File(apkFilePath);
            if (!apkFile.exists()) {
                //Log.e(TAG, "APK file does not exist: " + apkFilePath);
                Toast.makeText(context, "Arquivo APK não existe: " + apkFilePath, Toast.LENGTH_LONG).show();
                return;
            }
            in = new FileInputStream(apkFile);
            out = session.openWrite("base.apk", 0, -1);

            byte[] buffer = new byte[65536];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            session.fsync(out);
            //Log.d(TAG, "Finished writing APK file to session");
            Toast.makeText(context, "Finalizada a escrita do arquivo APK na sessão", Toast.LENGTH_SHORT).show();

            // Close the streams before committing
            in.close();
            out.close();

            in = null;
            out = null;

            Intent intent = new Intent(context, MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, sessionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(pendingIntent.getIntentSender());
            //Log.d(TAG, "Committed session for installation");
            Toast.makeText(context, "Sessão comitada para instalação", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //Log.e(TAG, "IOException during package installation", e);
            session.abandon();
            Toast.makeText(context, "Falha na instalação do pacote", Toast.LENGTH_LONG).show();
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //Log.e(TAG, "Error closing input stream", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //Log.e(TAG, "Error closing output stream", e);
                }
            }
            session.close();
            //Log.d(TAG, "Session closed");
            Toast.makeText(context, "Sessão fechada", Toast.LENGTH_SHORT).show();
        }
    }
}