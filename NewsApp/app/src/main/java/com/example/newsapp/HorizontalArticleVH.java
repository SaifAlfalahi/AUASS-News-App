package com.example.newsapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.model.Article;
import com.example.newsapp.utility.ImageGenerator;
import com.squareup.picasso.Picasso;

import com.google.android.material.imageview.ShapeableImageView;

public class HorizontalArticleVH extends RecyclerView.ViewHolder
{
    TextView title;
    ShapeableImageView image;
    ImageView like;
    ImageView dislike;

    public HorizontalArticleVH(@NonNull View itemView)
    {
        super(itemView);

        title = itemView.findViewById(R.id.article_card_title);
        image = itemView.findViewById(R.id.article_card_img);
        //like = itemView.findViewById(R.id.article_card_like);
        //dislike = itemView.findViewById(R.id.article_card_dislike);
    }

    //bind data method sets the title in the list item to the corresponding title of the article, as well as its image
    public void bindData(Article article, HorizontalArticleAdapter.OnItemClickListener listener)
    {
        title.setText(article.title());
        image.setImageBitmap(ImageGenerator.GenerateImage(article));

        //set the click listener to the item
        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onClick(article);
            }
        });
    }
}
