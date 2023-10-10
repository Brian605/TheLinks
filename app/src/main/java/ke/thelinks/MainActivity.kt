package ke.thelinks

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ke.thelinks.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding:ActivityMainBinding
    private lateinit var mRefreshLayout:SwipeRefreshLayout
    private lateinit var mWebView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mRefreshLayout=binding.refresh
        initFreshLayout()
        mWebView=binding.webview
        initWebView()

    }

    private fun initFreshLayout() {
        mRefreshLayout.setOnRefreshListener(this)
        val colorPrimary = ContextCompat.getColor(this, R.color.primary)
        val colorAccent = ContextCompat.getColor(this, R.color.accent)
        val colorPrimaryDark = ContextCompat.getColor(this, R.color.primary)
        mRefreshLayout.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark)
    }

    private fun initWebView() {
        mWebView.webViewClient = CustWebViewClient()
        mWebView.webChromeClient = CustChromeClient()
        CookieManager.getInstance().setAcceptCookie(true)
        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        val cacheDirPath: String = filesDir.absolutePath + "checktool"
        webSettings.databasePath = cacheDirPath
        webSettings.setGeolocationEnabled(true)
        webSettings.defaultTextEncodingName = "utf-8"
        var userAgentString = webSettings.userAgentString
        userAgentString += " UDAndroid/" + this.packageManager.getPackageInfo(packageName,0).versionName
        webSettings.userAgentString = userAgentString
        webSettings.builtInZoomControls = true
        webSettings.setSupportZoom(true)
        webSettings.displayZoomControls = false
        Log.d("WebviewFragment UA", webSettings.userAgentString)
        mWebView.loadUrl("https://thelinks.co.ke/")
    }
    inner class CustChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            Log.d("CustChromeClient", "newProgress:$newProgress")
            if (newProgress == 100) {
                mRefreshLayout.isRefreshing = false
            }
            super.onProgressChanged(view, newProgress)
        }
    }

    inner class CustWebViewClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            Log.d("CustWebViewClient", "error:$error")
            mRefreshLayout.isRefreshing = false
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            Log.d("CustWebViewClient", "errorResponse:" + errorResponse.data)
            mRefreshLayout.isRefreshing = false
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            mRefreshLayout.isRefreshing = true
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            mRefreshLayout.isRefreshing = false
            super.onPageFinished(view, url)
        }
    }

    override fun onRefresh() {
        mWebView.reload()
    }

}

