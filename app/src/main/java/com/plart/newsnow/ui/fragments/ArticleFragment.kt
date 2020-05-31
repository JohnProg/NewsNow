package com.plart.newsnow.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar

import com.plart.newsnow.databinding.FragmentArticleBinding
import com.plart.newsnow.ui.NewsActivity
import com.plart.newsnow.ui.NewsViewModel

class ArticleFragment : Fragment() {

    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var fragmentArticleBinding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentArticleBinding = FragmentArticleBinding.inflate(layoutInflater)
        return fragmentArticleBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        val article = args.article
        fragmentArticleBinding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        fragmentArticleBinding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

}
