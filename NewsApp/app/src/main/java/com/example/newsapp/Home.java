package com.example.newsapp;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newsapp.controller.ArticleController;
import com.example.newsapp.model.Article;
import com.example.newsapp.model.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment
{
    RecyclerView topNewsRV, recentRV;
    List<Article> articles;
    HorizontalArticleAdapter topArticleAdapter, recentArticleAdapter;
    public Home() {}

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //initialize array of top articles
        topNewsRV = view.findViewById(R.id.top_news_rv);
        recentRV = view.findViewById(R.id.recent_rv);
        articles = new ArrayList<>();

        //Get articles from online and send them to Detail Activity on click
        new ArticleController(getContext()).getArticles(new User("madyan2435@gmail.com", "madyan1726354"),
                article -> {
                    topArticleAdapter = new HorizontalArticleAdapter(articles, clicked_article -> {
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtra("article id", clicked_article.id());
                        startActivity(intent);
                    });
                    topArticleAdapter.addArticle(article);
                    topNewsRV.setAdapter(topArticleAdapter);
                    recentRV.setAdapter(topArticleAdapter);
                }, false);


        //initialize and set the layout manager for the top articles recycle view to make it horizontal
        LinearLayoutManager topNewsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topNewsRV.setLayoutManager(topNewsLayoutManager);

        //initialize and set the layout manager for the recent articles recycle view to make it horizontal
        LinearLayoutManager recentNewsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recentRV.setLayoutManager(recentNewsLayoutManager);

        return view;
    }

}