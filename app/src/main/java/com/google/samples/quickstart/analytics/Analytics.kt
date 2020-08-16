package com.google.samples.quickstart.analytics

import android.app.Activity
import android.webkit.JavascriptInterface
import br.com.diatonicmap.app.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker
import org.json.JSONObject

class Analytics(context : Activity) {
    private var ct: Activity = context
    private var ga: GoogleAnalytics = GoogleAnalytics.getInstance(context)
    private var tracker: Tracker = ga.newTracker(R.xml.global_tracker)


    fun startAnalytics() {

        //Send a page view
        tracker.setPage("/app.html")
        tracker.setScreenName("Diatonic App")
        tracker.setTitle("Diatonic App")

        ga.reportActivityStart(ct);

        tracker.send(ScreenViewBuilder().build())
    }

    fun endAnalytics() {
        ga.reportActivityStop(ct);
    }

    fun sendEvent(category: String, action: String, label:String, nonInteractive: Boolean) {
        tracker
            .send(EventBuilder()
            .setCategory(category)
            .setAction(action)
            .setAction(label)
            .setNonInteraction(nonInteractive)
            .build())
    }

    @JavascriptInterface
    fun logEvent(jsonParams: String) {
        val jsonObject = JSONObject(jsonParams)
        val category = jsonObject["2"]
        val action = jsonObject["3"]
        val label = jsonObject["4"]

        var nonInteractive = false
        if( jsonObject.has("5") )
            nonInteractive = true

        sendEvent(
            category as String,
            action as String,
            label as String,
            nonInteractive
        )
    }
}
