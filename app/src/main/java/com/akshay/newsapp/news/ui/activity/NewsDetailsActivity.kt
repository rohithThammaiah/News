package com.akshay.newsapp.news.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.akshay.newsapp.R
import com.akshay.newsapp.core.ui.ViewState
import com.akshay.newsapp.core.ui.base.BaseActivity
import com.akshay.newsapp.core.ui.compose.NewsTheme
import com.akshay.newsapp.news.storage.entity.NewsArticleDb
import com.akshay.newsapp.news.ui.viewmodel.NewsArticleViewModel
import dev.chrisbanes.accompanist.coil.CoilImage

const val NEWS_ARG_ARTICLE_ID = "articleId"

class NewsDetailsActivity : BaseActivity() {

    private val articleId: Int by lazy {
        intent.getIntExtra(NEWS_ARG_ARTICLE_ID, -1)
    }

    private val newsArticleViewModel: NewsArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsTheme {
                Scaffold(topBar = {
                    TopAppBar(
                            title = {},
                            backgroundColor = MaterialTheme.colors.primary,
                            navigationIcon = { IconButton(onClick = { finish() }) {
                                Icon(Icons.Filled.ArrowBack) }
                            }
                    )
                }, bodyContent = {
                        newsDetailsScreen(newsArticleViewModel = newsArticleViewModel, articleId)
                })
            }
        }
    }
}

@Composable
fun newsDetailsScreen(newsArticleViewModel: NewsArticleViewModel, newsId: Int) {
    val viewState by newsArticleViewModel.getNewsArticle(articleId = newsId).observeAsState(ViewState.loading())
    when (viewState) {
        is ViewState.Loading -> {
            loadingIndicator()
        }
        is ViewState.Error -> {
            errorView((viewState as ViewState.Error<NewsArticleDb>).message)
        }
        is ViewState.Success -> {
            ScrollableColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                with((viewState as ViewState.Success<NewsArticleDb>).data) {
                    Spacer(modifier = Modifier.preferredHeight(8.dp))
                    CoilImage(data = urlToImage ?: R.drawable.tools_placeholder,
                            modifier = Modifier
                                    .heightIn(min = 180.dp)
                                    .fillMaxWidth()
                                    .clip(shape = MaterialTheme.shapes.medium)
                    )
                    Spacer(Modifier.preferredHeight(16.dp))
                    Text(text = title ?: "", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.preferredHeight(8.dp))
                    Text(text = content ?: "", style = MaterialTheme.typography.body1)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun loadingIndicator() {
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                strokeWidth = 6.dp,
                modifier = Modifier.preferredSize(64.dp)
        )
    }
}

@Composable
fun errorView(message: String) {
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message, style = MaterialTheme.typography.body1)
    }
}

@Preview(showBackground = true)
@Composable
fun errorViewPreview() {
    errorView(message = "Something went wrong!")
}
