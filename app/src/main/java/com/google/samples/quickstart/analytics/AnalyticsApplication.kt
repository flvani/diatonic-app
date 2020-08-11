package com.google.samples.quickstart.analytics

import android.content.Context
import android.webkit.JavascriptInterface
import br.com.diatonicmap.app.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import com.google.android.gms.analytics.Tracker
import org.json.JSONObject

class AnalyticsApplication(context : Context) {

    private var tracker: Tracker = GoogleAnalytics.getInstance(context).newTracker(R.xml.global_tracker)

    companion object {

        private var instance : AnalyticsApplication? = null

        fun getInstance(context: Context) : AnalyticsApplication {
            if (instance == null) {
                instance = AnalyticsApplication(context)
                instance!!.sendInitAnalytics()
            }
            return instance!!
        }
    }

    fun sendInitAnalytics() {
        //Send a page view
        tracker.setPage("app.html")
        tracker.setScreenName("main")
        tracker.setTitle("Diatonic App")
        tracker.send(HitBuilders.ScreenViewBuilder().build())
    }

    @JavascriptInterface
    fun logEvent(jsonParams: String) {
        val jsonObject = JSONObject(jsonParams)
        val category = jsonObject["2"]
        val action = jsonObject["3"]
        val label = jsonObject["4"]

        var nonInteractive = false
        if(jsonObject.has("5") )
            nonInteractive = true

        tracker.send(
            EventBuilder()
            .setCategory(category as String?)
            .setAction(action as String?)
            .setLabel(label as String?)
            .setNonInteraction(nonInteractive)
            .build())
    }

    fun sendEvent(category: String, action: String, label:String) {
        tracker.send(HitBuilders.EventBuilder().setCategory(category).setAction(action).setAction(label).build())
    }

    init {
        //GoogleAnalytics.getInstance(context!!)
    }

}
