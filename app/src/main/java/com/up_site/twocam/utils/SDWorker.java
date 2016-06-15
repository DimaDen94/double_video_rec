package com.up_site.twocam.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Dmitry on 18.05.2016.
 */
public class SDWorker {
    private static final String TAG = "mActivityLogs";

    public static File createDirectory() {
        File directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "double_movies");
        if (!directory.exists())
            directory.mkdirs();
        return directory;
    }


    public static File generateFileUri(File directory, int pos) {
        if (pos == 0)
            return new File(directory.getPath() + "/" + "back_" + "movies_" + System.currentTimeMillis() + ".3gp");
        else
            return new File(directory.getPath() + "/" + "front_" + "movies_" + System.currentTimeMillis() + ".3gp");
    }


    public static void galleryAddPic(File file, Context c) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        c.sendBroadcast(mediaScanIntent);
    }
}
