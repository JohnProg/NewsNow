package com.plart.newsnow.data.repository

import com.plart.newsnow.data.db.ArticleDao
import com.plart.newsnow.data.network.NewsApi
import com.plart.newsnow.models.Article

class NewsRepository(
    private val articleDao: ArticleDao,
    private val newsApi: NewsApi
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        newsApi.searchNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = articleDao.upsert(article)

    fun getSavedNews() = articleDao.getAllArticles()

    suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)
}