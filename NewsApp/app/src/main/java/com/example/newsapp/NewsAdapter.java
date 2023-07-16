package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.newsapp.model.Article;
import com.example.newsapp.utility.ImageGenerator;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter
{
    private List<Article> articles;

    public NewsAdapter(@NonNull Context context, int resource, List<Article> articles) {
        super(context, resource, articles);
        this.articles = articles;
    }

    public void addArticle(Article article){
        articles.add(article);
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, parent, false);

        //get the views from the list item layout
        TextView title = view.findViewById(R.id.article_list_item_title);
        ShapeableImageView image = view.findViewById(R.id.article_list_item_img);

        //set the data to the views
        title.setText(articles.get(position).title());
        image.setImageBitmap(ImageGenerator.GenerateImage(articles.get(position)));

        return view;
    }

}
