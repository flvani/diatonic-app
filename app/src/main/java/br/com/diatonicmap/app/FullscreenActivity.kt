package br.com.diatonicmap.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader


class FullscreenActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    //private lateinit var mTracker: AnalyticsApplication

    //private var myUrl: String = "http://192.168.0.17:8080/app.html"
    //private var myUrl: String ="https://diatonicmap.com.br/app.html"
    //private var myUrl: String = "file:///android_asset/app.html"

    private var myUrl: String = "https://appassets.androidplatform.net/assets/app.html"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        requestWindowFeature(FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fullscreen)

        webView = findViewById(R.id.webview)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        } else {
            CookieManager.getInstance().setAcceptCookie(true)
        }

        webView.settings.displayZoomControls = false
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)

        hideSystemUI()

        //val mTracker = AnalyticsApplication(this)
        //webView.addJavascriptInterface(mTracker, "AnalyticsApplication")

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(myUrl)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onResume() {
        super.onResume()
        webView.evaluateJavascript(
            "(function() { window.dispatchEvent(onAppResumeEvent); })();",
            ValueCallback<String?> { })
    }

    override fun onStop() {
        super.onStop()
        webView.evaluateJavascript(
            "(function() { window.dispatchEvent(onAppStopEvent); })();",
            ValueCallback<String?> { })
    }

    override fun onPause() {
        super.onPause()
        webView.evaluateJavascript(
            "(function() { window.dispatchEvent(onAppStopEvent); })();",
            ValueCallback<String?> { })
    }
/* FLÁVIO aqui

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.getAction() === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        //return super.onKeyDown(keyCode, event)
        return true;
    }
*/

    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        webView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}

