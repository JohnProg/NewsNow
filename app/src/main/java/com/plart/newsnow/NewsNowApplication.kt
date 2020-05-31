package com.plart.newsnow

import android.app.Application
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.plart.newsnow.data.db.ArticleDatabase
import com.plart.newsnow.data.network.ConnectivityInterceptor
import com.plart.newsnow.data.network.ConnectivityInterceptorImpl
import com.plart.newsnow.data.network.NewsApi
import com.plart.newsnow.data.repository.NewsRepository
import com.plart.newsnow.ui.NewsViewModelProviderFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class NewsNowApplication: Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@NewsNowApplication))
        bind() from singleton { ArticleDatabase(instance()) }
        bind() from singleton { instance<ArticleDatabase>().getArticleDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { NewsApi(instance()) }
        bind() from singleton { NewsRepository(instance(), instance()) }
        bind() from provider { NewsViewModelProviderFactory(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
    }
}