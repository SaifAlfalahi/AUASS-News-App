package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.model.Article;

import java.util.List;

public class HorizontalArticleAdapter extends RecyclerView.Adapter
{
    //interface for recycle view item click
    public interface OnItemClickListener {
        void onClick(Article article);}

    private List<Article> articles;
    private OnItemClickListener listener;
    HorizontalArticleAdapter(List<Article> articles, OnItemClickListener listener)
    {
        this.articles = articles;
        this.listener = listener;
    }

    public void addArticle(Article article){
        articles.add(article);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //link the article_card layout to each list item in the recycle view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card, parent, false);

        //instantiate an object of the HorizontalArticleVH
        HorizontalArticleVH vh = new HorizontalArticleVH(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        //use the HorizontalArticleVH object to bind the layout with the corresponding data.
        ((HorizontalArticleVH) holder).bindData(articles.get(position), listener);
    }

    @Override
    public int getItemCount()
    {
        return articles.size();
    }
}
