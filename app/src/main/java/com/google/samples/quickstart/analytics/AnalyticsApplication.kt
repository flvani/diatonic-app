package com.google.samples.quickstart.analytics

import android.content.Context
import br.com.diatonicmap.app.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

class AnalyticsApplication(context : Context) {

    private var tracker: Tracker = GoogleAnalytics.getInstance(context!!).newTracker(R.xml.global_tracker)

    companion object {

        private var instance : AnalyticsApplication? = null

        fun getInstance(context: Context) : AnalyticsApplication {
            if (instance == null) {
                instance = AnalyticsApplication(context)
                instance!!.sendInitAnalytics(context)
            }
            return instance!!
        }
    }

    fun sendEvent(category: String, action: String) {
        tracker.send(HitBuilders.EventBuilder().setCategory(category).setAction(action).build())
    }

    fun sendInitAnalytics(context: Context) {
        tracker.send(HitBuilders.EventBuilder()
            .setCategory("App")
            .setAction("Create")
            .setLabel(context.packageName)
            .build())
    }


    init {
        //GoogleAnalytics.getInstance(context!!)
    }


}
