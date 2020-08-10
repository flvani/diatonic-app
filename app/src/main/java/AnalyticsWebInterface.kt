import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.firebase.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import org.json.JSONException
import org.json.JSONObject

class AnalyticsWebInterface(context: Context?) {
    private val mAnalytics: FirebaseAnalytics

    @JavascriptInterface
    fun logEvent(name: String, jsonParams: String) {
        LOGD("logEvent:$name")
        mAnalytics.logEvent(name, bundleFromJson(jsonParams))
    }

    /*

    @JavascriptInterface
    public void setUserProperty(String name, String value) {
        LOGD("setUserProperty:" + name);
        mAnalytics.setUserProperty(name, value);
    }
*/
    private fun LOGD(message: String) {
        // Only log on debug builds, for privacy
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    private fun bundleFromJson(json: String): Bundle {
        // [START_EXCLUDE]
        if (TextUtils.isEmpty(json)) {
            return Bundle()
        }
        val result = Bundle()
        try {
            val jsonObject = JSONObject(json)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = jsonObject[key]
                if (value is String) {
                    result.putString(key, value)
                } else if (value is Int) {
                    result.putInt(key, value)
                } else if (value is Double) {
                    result.putDouble(key, value)
                } else {
                    Log.w(
                        TAG,
                        "Value for key $key not one of [String, Integer, Double]"
                    )
                }
            }
        } catch (e: JSONException) {
            Log.w(
                TAG,
                "Failed to parse JSON, returning empty Bundle.",
                e
            )
            return Bundle()
        }
        return result
        // [END_EXCLUDE]
    }

    companion object {
        const val TAG = "AnalyticsWebInterface"
    }

    init {
        mAnalytics = FirebaseAnalytics.getInstance(context!!)
    }
}