package br.com.diatonicmap.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.view.KeyEvent
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader

class DiatonicApp : AppCompatActivity() {

    private lateinit var webView: WebView
    //private var pressedTime: Long = 0

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

        this.hideSystemUI()

        webView.addJavascriptInterface(this, "DiatonicApp")

        webView.run {

            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.domStorageEnabled = true
            settings.displayZoomControls = false
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)

            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }
            }

            if (savedInstanceState != null) {
                restoreState(savedInstanceState)
            } else {
                loadUrl(myUrl)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onStop() {
        super.onStop()
        webView.evaluateJavascript(
            "(function() { window.dispatchEvent(onAppStopEvent); })();"
        ) { }
    }

    override fun onPause() {
        super.onPause()
        webView.evaluateJavascript(
            "(function() { window.dispatchEvent(onAppStopEvent); })();"
        ) { }
    }

override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    webView.evaluateJavascript(
                        "(function() { window.dispatchEvent(onAppBack); })();"
                    ) { }
                    return false
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


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

    @JavascriptInterface
    fun closeApp() {
        finish()
    }

    @JavascriptInterface
    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    @JavascriptInterface
    fun printPage(jobName: String) {
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;*/
        webView.post {
            val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = PrintDocumentAdapterWrapper(  webView.createPrintDocumentAdapter(jobName), webView )
            val builder = PrintAttributes.Builder()
            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            val printJob: PrintJob = printManager.print(jobName, printAdapter, builder.build())
            if (printJob.isCompleted) {
                Toast.makeText(applicationContext, R.string.print_complete, Toast.LENGTH_LONG).show()
            } else if (printJob.isFailed) {
                Toast.makeText(applicationContext, R.string.print_failed, Toast.LENGTH_LONG).show()
            }
        }
    }
}

class PrintDocumentAdapterWrapper(private val delegate: PrintDocumentAdapter, private val w: WebView):  PrintDocumentAdapter() {

    override fun onFinish() {
        delegate.onFinish()
        w.run {
            evaluateJavascript(
                "(function() { window.dispatchEvent(onAppEndPreview); })();"
            ) { }
        }
    }

    override fun onLayout(
        p0: PrintAttributes?,
        p1: PrintAttributes?,
        p2: CancellationSignal?,
        p3: LayoutResultCallback?,
        p4: Bundle?
    ) {
        delegate.onLayout(p0,p1,p2,p3,p4)
    }

    override fun onWrite(
        p0: Array<out PageRange>?,
        p1: ParcelFileDescriptor?,
        p2: CancellationSignal?,
        p3: WriteResultCallback?
    ) {
        delegate.onWrite(p0,p1,p2,p3)
    }
}