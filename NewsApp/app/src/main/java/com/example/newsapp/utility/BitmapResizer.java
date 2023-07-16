package com.example.newsapp.utility;

import android.content.Context;
import android.graphics.Bitmap;
public class BitmapResizer {

    public static Bitmap resizeBitmap(Context context, Bitmap image, int targetSizeDp) {
        // Step 1: Convert dp to pixels
        int targetSizePx = dpToPx(context, targetSizeDp);

        // Step 2: Load the original Bitmap from file
        Bitmap originalBitmap = image;

        // Step 3: Calculate the aspect ratio
        float aspectRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();

        // Step 4: Calculate the new dimensions
        int newWidth, newHeight;
        if (originalBitmap.getWidth() >= originalBitmap.getHeight()) {
            // Landscape or square image
            newWidth = targetSizePx;
            newHeight = Math.round(newWidth / aspectRatio);
        } else {
            // Portrait image
            newHeight = targetSizePx;
            newWidth = Math.round(newHeight * aspectRatio);
        }

        // Step 5: Resize the Bitmap
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }

    private static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

