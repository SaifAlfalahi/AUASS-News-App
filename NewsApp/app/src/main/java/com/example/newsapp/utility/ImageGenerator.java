package com.example.newsapp.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.newsapp.model.Article;
import com.example.newsapp.model.Image;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ImageGenerator {

    public static ArrayList<Bitmap> GenerateImages(Article article) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        int numImages = article.imagesLength();
        if (article != null && article.imagesLength() > 0) {
            for (int i = 0; i < numImages; i++) {
                Image image = article.images(i);
                ByteBuffer byteBuffer = image.bytesAsByteBuffer();
                byte[] imageData = new byte[byteBuffer.remaining()];
                byteBuffer.get(imageData);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                if (bitmap != null) {
                    bitmaps.add(bitmap);
                }
            }
        }

        return bitmaps;
    }

    public static Bitmap GenerateImage(Article article){
        if(article.imagesLength() == 0){
            return null;
        }
        Image image = article.images(0);
        ByteBuffer byteBuffer = image.bytesAsByteBuffer();
        byte[] imageData = new byte[byteBuffer.remaining()];
        byteBuffer.get(imageData);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return bitmap;
    }

}
