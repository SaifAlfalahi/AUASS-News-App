package com.example.newsapp.controller;

import com.example.newsapp.model.Article;

import java.util.ArrayList;

public interface ArticlesCallback {
    void onSuccess(Article article);
    void onFailure(String errorMessage);
}
