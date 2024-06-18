package dev.trindade.shizuku.package_installer;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.view.*;
import android.widget.*;

import java.io.*;

public class PackageInstallerUtils {

    public static void installPackage(Context context, String apkFilePath) throws IOException {
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);

        int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(new File(apkFilePath));
            out = session.openWrite("base.apk", 0, -1);

            byte[] buffer = new byte[65536];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            session.fsync(out);

            // Close the streams before committing
            in.close();
            out.close();

            in = null;
            out = null;

            Intent intent = new Intent(context, MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, sessionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(pendingIntent.getIntentSender());
        } catch (IOException e) {
            session.abandon();
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context,  e.toString(), 4000).show();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context,  e.toString(), 4000).show();
                }
            }
            session.close();
        }
    }
}