package com.example.newsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.newsapp.controller.ArticleController;
import com.example.newsapp.model.Article;
import com.example.newsapp.model.User;
import com.example.newsapp.utility.ImageGenerator;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity
{
    ImageView backBtn;
    ShapeableImageView detailImg;
    ArticleController articleController;
    LinearLayout resource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        resource = findViewById(R.id.article_detail_view);

        Intent intent = getIntent();
        articleController = new ArticleController(this);
        articleController.getArticle(
                new User("madyan2435@gmail.com", "madyan1726354"),
                intent.getIntExtra("article id", 0),
                article -> {
                    detailImg.setImageBitmap(ImageGenerator.GenerateImage(article));
                    articleController.getArticleView(resource, article, Gravity.LEFT);
                }
        );

        //set the custom action bar layout as the action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_detail);
        View actionBarView = getSupportActionBar().getCustomView();

        //set variables to their corresponding views
        detailImg = findViewById(R.id.detail_img);
        backBtn = actionBarView.findViewById(R.id.detail_action_back_btn);

        //back button takes back to the main activity
        backBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent1);
        });
    }
}