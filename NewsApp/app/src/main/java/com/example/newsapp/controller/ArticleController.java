package com.example.newsapp.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.Home;
import com.example.newsapp.MainActivity;
import com.example.newsapp.News;
import com.example.newsapp.R;
import com.example.newsapp.model.Article;
import com.example.newsapp.model.Image;
import com.example.newsapp.model.Status;
import com.example.newsapp.model.User;
import com.example.newsapp.utility.ImageGenerator;
import com.example.newsapp.utility.ImagePlaceholderFinder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.flatbuffers.FlatBufferBuilder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ArticleController {
    private final Context context;

    public Context getContext() {
        return context;
    }

    public ArticleController(Context context) {
        this.context = context;
    }

    public void getArticleView(LinearLayout resource, Article article, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(30, 0, 30, 30);
        resource.setBackground(resource.getResources().getDrawable(R.drawable.round_corner_20));


        //Add image title to our Linear Layout
        resource.addView(getTextView(article.title(), true, layoutParams, gravity));

        //Get article attributes and generate images using the image bytes retrieved from firebase.
        String text = article.text();
        ArrayList<Bitmap> bitmaps = ImageGenerator.GenerateImages(article);
        
        //Get image placeholder index by using Regex to find patterns that match '{img_n}'
        //where n is any natural number
        List<Integer> imgPlaceholderIndices = ImagePlaceholderFinder.GetIndeces("\\{img_\\d+\\}", article.text());

        //Create article by adding images at image placeholder locations ex: {img_0}
        //and also adding the text before the image placeholder using substring and regex
        //to replace the image placeholder text
        int imgIndex = 0;
        int startIndex = 0;
        for (int i = 0; i < imgPlaceholderIndices.size();) {
            text = text.replaceFirst("\\{img_\\d+\\}", "");
            String textBeforeImage = text.substring(startIndex, Math.min(imgPlaceholderIndices.get(i), text.length()-1));
            if (!textBeforeImage.isEmpty()) {
                resource.addView(getTextView(textBeforeImage, false, layoutParams, gravity));
            }

            resource.addView(getImageView(bitmaps.get(imgIndex), layoutParams));

            imgIndex++;
            startIndex = imgPlaceholderIndices.get(i);
            imgPlaceholderIndices = ImagePlaceholderFinder.GetIndeces("\\{img_\\d+\\}", text);
        }

        String remainingText = null;
        if(startIndex <= text.length()){
            remainingText = text.substring(startIndex);
        }
        if (remainingText!=null) {
            resource.addView(getTextView(remainingText, false, layoutParams, gravity));
        }

        if (imgIndex < imgPlaceholderIndices.size()) {
            for (int i = imgIndex; i < imgPlaceholderIndices.size(); i++) {
                text = text.replaceFirst("\\{img_\\d+\\}", "");
                resource.addView(getImageView(bitmaps.get(imgIndex), layoutParams));
            }
        }

        resource.addView(getTextView("Article posted on: " + article.date(), false, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT), Gravity.END));
    }

    public void deleteArticle(int article_id){
        StringBuilder fileName = new StringBuilder();
        fileName.append("articles/article_");
        fileName.append(article_id);
        fileName.append(".bin");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(fileName.toString());
        storageRef.delete().addOnSuccessListener(e->{
            Toast.makeText(context, "Article Successfully deleted", Toast.LENGTH_LONG).show();
            Log.i("Article Deletion", "Successfully deleted article with id: " + article_id);
        }).addOnFailureListener(e->{
            Log.e("Article Deletion", e.getMessage());
        });
    }

    private TextView getTextView(String text, boolean isTitle, ViewGroup.LayoutParams layoutParams, int gravity) {
        int textSize = isTitle ? 36 : 24;
        int textColor = context.getResources().getColor(R.color.dark);

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        textView.setGravity(gravity);
        textView.setLayoutParams(layoutParams);

        return textView;
    }

    public Article generateArticle(String title, String content, ArrayList<Bitmap> images, byte status, String author){
        FlatBufferBuilder articleBuilder = new FlatBufferBuilder();

        if(images.size() == 0){
            images.add(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.auass)
            );
        }

        int titleOffset = articleBuilder.createString(title);
        int contentOffset = articleBuilder.createString(content);

        //Take current date as date of article
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        int dateOffset = articleBuilder.createString(formattedDate);

        int authorOffset = articleBuilder.createString(author);

        //Add images
        int imagesOffset = 0;
        if (images.size() > 0) {
            int[] imagesOffsets = new int[images.size()];
            for (int i = 0; i < images.size(); i++) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                images.get(i).compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                int byteVectorOffset = Image.createBytesVector(articleBuilder, byteArray);
                int imageOffset = Image.createImage(articleBuilder, byteVectorOffset);
                imagesOffsets[i] = imageOffset;
            }
            imagesOffset = Article.createImagesVector(articleBuilder, imagesOffsets);
            Article.startArticle(articleBuilder);
            Article.addImages(articleBuilder, imagesOffset);
            int offset = Article.endArticle(articleBuilder);
            Article.finishArticleBuffer(articleBuilder, offset);
        }

        int articleOffset = Article.createArticle(articleBuilder,
                status,
                Math.abs((int) System.currentTimeMillis()),
                titleOffset,
                contentOffset,
                imagesOffset,
                authorOffset,
                0,
                0,
                dateOffset
        );
        articleBuilder.finish(articleOffset);
        ByteBuffer buffer = articleBuilder.dataBuffer();
        return Article.getRootAsArticle(buffer);
    }

    private ImageView getImageView(Bitmap image, ViewGroup.LayoutParams layoutParams) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(image);
        return imageView;
    }

    public void getArticle(User user, int id, final ArticlesCallback callback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = user.getEmail();
        String password = user.getPassword();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Create filename
                StringBuilder fileName = new StringBuilder();
                fileName.append("articles/article_");
                fileName.append(String.valueOf(id));
                fileName.append(".bin");
                Log.i("Firebase Authentication", "User successfully authenticated");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                //Get File in folder article
                StorageReference storageRef = storage.getReference().child(fileName.toString());
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    new DownloadTask(new ArrayList<>(), callback, false).execute(downloadUrl);
                }).addOnFailureListener(e -> Log.e("Failed to get download URL", e.getMessage()));
            } else {
                String error_message = task.getException().getMessage() != null ? task.getException().getMessage() : "Error occurred with Firebase Authentication";
                Log.e("Firebase Authentication", error_message);
            }
        });
    }

    public void getArticles(User user, final ArticlesCallback callback, boolean pendingArticles) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = user.getEmail();
        String password = user.getPassword();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("Firebase Authentication", "User successfully authenticated");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("articles");

                storageRef.listAll()
                        .addOnSuccessListener(listResult -> {
                            List<StorageReference> items = listResult.getItems();
                            List<Article> articles = new ArrayList<>();

                            for (StorageReference item : items) {
                                item.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    new DownloadTask(articles, callback, pendingArticles).execute(downloadUrl);
                                }).addOnFailureListener(e -> Log.e("Failed to get download URL", e.getMessage()));
                            }
                        })
                        .addOnFailureListener(exception -> Log.e("Error viewing articles", exception.getMessage()));
            } else {
                String error_message = task.getException().getMessage() != null ? task.getException().getMessage() : "Error occurred with Firebase Authentication";
                Log.e("Firebase Authentication", error_message);
            }
        });
    }

    private static class DownloadTask extends AsyncTask<String, Void, byte[]> {
        private final List<Article> articles;
        private final ArticlesCallback callback;
        private boolean getPendingArticles;

        public DownloadTask(List<Article> articles, ArticlesCallback callback, boolean getPendingArticles) {
            this.articles = articles;
            this.callback = callback;
            this.getPendingArticles = getPendingArticles;
        }

        @Override
        protected byte[] doInBackground(String... urls) {
            byte[] serializedData = null;

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                serializedData = outputStream.toByteArray();

                // Close the connections and streams
                outputStream.close();
                inputStream.close();
                connection.disconnect();
            } catch (Exception e) {
                Log.e("Error downloading file", e.getMessage());
            }

            return serializedData;
        }

        @Override
        protected void onPostExecute(byte[] serializedData) {
            if (serializedData != null) {
                // Deserialize the FlatBuffer object
                Article article = deserializeArticle(serializedData);
                boolean isPending = com.example.newsapp.model.Status.PENDING == article.status();
                if(getPendingArticles && isPending){
                    articles.add(article);
                    callback.onArticleAdded(article);
                }else if(!getPendingArticles && !isPending){
                    articles.add(article);
                    callback.onArticleAdded(article);
                }
            }
        }

        private Article deserializeArticle(byte[] serializedData) {
            // Use FlatBuffers API to deserialize the object
            ByteBuffer buffer = ByteBuffer.wrap(serializedData);
            Article article = Article.getRootAsArticle(buffer);

            return article;
        }
    }

    public interface ArticlesCallback {
        void onArticleAdded(Article article);
    }
}
