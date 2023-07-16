package com.example.newsapp;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.newsapp.controller.ArticleController;
import com.example.newsapp.model.Article;
import com.example.newsapp.model.Status;
import com.example.newsapp.model.User;
import com.example.newsapp.utility.BitmapResizer;
import com.example.newsapp.utility.ImageGenerator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Publish extends Fragment
{
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int IMG_SIZE = 250;
    Article article;
    User user;
    int offset = 0;
    ArrayList<Bitmap> images = new ArrayList<>();
    List<Article> pending_articles;
    EditText articleTitle;
    EditText articleContent;
    ImageButton imageButton;
    Button articleButton;
    ArticleController articleController;
    public Publish() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        //Get Firebase Storage instance
        articleTitle = view.findViewById(R.id.article_title);
        articleContent = view.findViewById(R.id.article_content);
        articleButton = view.findViewById(R.id.upload_article);
        imageButton = view.findViewById(R.id.upload_img);
        article = new Article();
        articleController = new ArticleController(getContext());
        pending_articles = new ArrayList<>();

        //Render only pending list of articles for admin, if the user is an admin
        ConstraintLayout userView = view.findViewById(R.id.user_view);
        user = new User("madyan2435@gmail.com", "madyan1726354");
        //Uncomment to see admin view of application
        //user.setAdmin();
        if(user.isAdmin())
        {
            for(int i=0; i<userView.getChildCount(); i++)
            {
                View childView = userView.getChildAt(i);
                childView.setVisibility(View.GONE);
            }
            TextView adminTitle = view.findViewById(R.id.admin_title);
            RecyclerView adminView = view.findViewById(R.id.admin_view);
            adminView.setVisibility(View.VISIBLE);
            adminTitle.setVisibility(View.VISIBLE);
            VerticalArticleAdapter articleAdapter = new VerticalArticleAdapter(pending_articles, clicked_article->{
                previewArticleAdmin(clicked_article);
            });
            articleController.getArticles(user, article -> {
                articleAdapter.addArticle(article);
                adminView.setAdapter(articleAdapter);
            }, true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            adminView.setLayoutManager(layoutManager);
        }



        imageButton.setOnClickListener(e -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        articleButton.setOnClickListener(article_preview -> {
            article = articleController.generateArticle(articleTitle.getText().toString(),
                    articleContent.getText().toString(),
                    images,
                    Status.PENDING,
                    "Madyan Omar");
            previewArticle(article);
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Adding the text written so far to our string array
            // and selected image URI to the article. We also keep
            // track of the position of the text cursor using offset variable
            String img_placeholder = "\n{img_" + images.size() + "}\n";
            articleContent.append(img_placeholder);
            offset = articleContent.getText().toString().length();
            try {
                Bitmap img_bitmap = BitmapResizer.resizeBitmap(getContext(),
                        MediaStore.Images.Media.getBitmap(
                                getActivity().getContentResolver(),
                                selectedImageUri), IMG_SIZE);
                if(images == null){
                    images = new ArrayList<>();
                }
                images.add(img_bitmap);
            } catch (Exception e) {
                Log.e("Bitmap conversion error", e.getMessage());
            }
        }
    }

    //Do this
    private void previewArticle(@NonNull Article article){
        Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Article Preview");
        dialog.setContentView(R.layout.news_preview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        generateView(article, dialog.findViewById(R.id.preview_layout), false);

        dialog.show();
    }

    private void previewArticleAdmin(@NonNull Article article){
        Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Article Preview");
        dialog.setContentView(R.layout.news_preview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        generateView(article, dialog.findViewById(R.id.preview_layout), true);

        dialog.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void generateView(@NonNull Article article, LinearLayout resource, boolean isAdmin){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(30, 0, 30, 5);

        new ArticleController(getContext()).getArticleView(resource, article, Gravity.CENTER_HORIZONTAL);

        FirebaseApp.initializeApp(getContext());
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if(isAdmin){
            LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_params.setMargins(10,10,10,10);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageButton accept = new ImageButton(getContext());
            accept.setLayoutParams(
                    new ViewGroup.LayoutParams(100, 100)
            );
            accept.setBackground(getResources().getDrawable(R.drawable.check));
            accept.setPadding(20, 10, 20, 10);
            accept.setOnClickListener(e->{
                //Create an article with same values but change status to Approved
                //Upload the new article to firebase and delete the previous article with status set to Pending
                ArrayList<Bitmap> images = ImageGenerator.GenerateImages(article);
                uploadArticle(articleController.generateArticle(article.title(), article.text(), images, Status.APPROVED, "Madyan Omar"), storage);
                articleController.deleteArticle(article.id());
            });

            ImageButton reject = new ImageButton(getContext());
            reject.setLayoutParams(
                    new ViewGroup.LayoutParams(100, 100)
            );
            reject.setBackground(getResources().getDrawable(R.drawable.x));
            reject.setPadding(20, 10, 20, 10);
            reject.setOnClickListener(e->{
                articleController.deleteArticle(article.id());
            });
            linearLayout.addView(accept);
            linearLayout.addView(reject);
            resource.addView(linearLayout);
        }else{
            Button button = new Button(getContext());
            button.setText(R.string.article_upload);
            button.setLayoutParams(layoutParams);
            button.setPadding(10, 0,10,0);
            button.setBackground(getResources().getDrawable(R.drawable.round_corner_50));
            button.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.primary_light));

            button.setOnClickListener(e->{
                uploadArticle(article, storage);
            });

            resource.addView(button);
        }
    }

    private void uploadArticle(@NonNull Article article, @NonNull FirebaseStorage storage) {
        //Authorize User
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = "madyan2435@gmail.com";
        String password = "madyan1726354";

        //Check if sign in succeeded
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(e->{
            if(e.isSuccessful()){
                Log.i("Firebase Authentication", "User successfully authenticated");
            }else{
                Log.e("Firebase Authentication", e.getResult().toString());
            }
        });

        // Generate a unique filename for the article
        String filename = "article_" + article.id() + ".bin";

        // Get a reference to the Firebase Storage location where you want to store the article
        StorageReference storageRef = storage.getReference().child("articles").child(filename);

        // Convert the article object to Flat Buffer object
        ByteBuffer buffer = article.getByteBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        // Upload the data to Firebase Storage
        UploadTask uploadTask = storageRef.putBytes(data);

        // Listen for the upload success or failure
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("Upload", "Upload succeeded");
                Toast.makeText(getView().getContext(), "Upload succeeded", Toast.LENGTH_LONG).show();

                //Switch from Publish Fragment to News Fragment to view uploaded article
                int container = R.id.fragment_container;
                FragmentTransaction publishFT = getActivity().getSupportFragmentManager().beginTransaction();
                publishFT.replace(container, new News());
                publishFT.commit();
            } else {
                // An error occurred during the upload
                Log.e("Upload", "Upload failed: " + task.getException().getMessage());
                Toast.makeText(getView().getContext(), "Upload failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}