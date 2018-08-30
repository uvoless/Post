package com.juiyingchiu.post;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;


class Common {


    public static Bitmap downSize(Bitmap srcPicture, int newSize) {

        if (newSize < 20) {
            newSize = 20;
        }
        int srcWidth = srcPicture.getWidth();
        int srcHeight = srcPicture.getHeight();
        int longer = Math.max(srcWidth, srcHeight);
        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);

            srcPicture = Bitmap.createScaledBitmap(srcPicture, dstWidth, dstHeight, false);
            System.gc();
        }
        return srcPicture;
    }


    public static final int REQ_EXTERNAL_STORAGE = 0;

    public static void askPermissions(Activity activity, String[] permissions, int requestCode) {
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }





}
