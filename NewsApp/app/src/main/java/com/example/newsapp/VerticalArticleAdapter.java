package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.model.Article;

import java.util.List;

public class VerticalArticleAdapter extends RecyclerView.Adapter {


    public interface OnItemClickListener {
        void onClick(Article article);}

    private List<Article> articles;
    private OnItemClickListener listener;


    VerticalArticleAdapter(List<Article> articles, OnItemClickListener listener)
    {
        this.articles = articles;
        this.listener = listener;
    }

    public void addArticle(Article article){
        articles.add(article);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VerticalArticleVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VerticalArticleVH) holder).bindData(articles.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
