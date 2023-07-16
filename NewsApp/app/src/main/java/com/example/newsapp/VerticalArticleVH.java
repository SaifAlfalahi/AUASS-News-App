package com.example.newsapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.model.Article;
import com.example.newsapp.utility.ImageGenerator;

public class VerticalArticleVH extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView content;
    private ImageView image;
    public VerticalArticleVH(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.article_list_item_title);
        image = itemView.findViewById(R.id.article_list_item_img);
    }


    public void bindData(Article article, VerticalArticleAdapter.OnItemClickListener listener) {
        title.setText(article.title());
        image.setImageBitmap(ImageGenerator.GenerateImage(article));

        itemView.setOnClickListener(v -> listener.onClick(article));
    }
}
