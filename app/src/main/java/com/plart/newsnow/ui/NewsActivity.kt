package com.plart.newsnow.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.plart.newsnow.R
import com.plart.newsnow.core.services.TrackingService
import kotlinx.android.synthetic.main.activity_news.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class NewsActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    lateinit var viewModel: NewsViewModel
    private val factory: NewsViewModelProviderFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}
