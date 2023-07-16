package com.example.newsapp;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.newsapp.controller.ArticleController;
import com.example.newsapp.model.Article;
import com.example.newsapp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class News extends Fragment
{
    ListView newsListView;
    List<Article> articles;
    NewsAdapter newsAdapter;
    public News() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsListView = view.findViewById(R.id.news_list_view);
        articles = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), R.layout.article_list_item, articles);
        new ArticleController(getContext()).getArticles(new User("madyan2435@gmail.com", "madyan1726354"),
                article -> {
                    newsAdapter.addArticle(article);
                    newsListView.setAdapter(newsAdapter);
                    newsListView.setOnItemClickListener((parent, view2, position, id) -> {
                                Intent intent = new Intent(getContext(), DetailActivity.class);
                                intent.putExtra("article id", article.id());
                                startActivity(intent);
                    });}, false);
        return view;
    }
}