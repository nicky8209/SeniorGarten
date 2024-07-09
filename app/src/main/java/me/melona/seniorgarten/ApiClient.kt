package me.melona.seniorgarten

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class ApiClient {
    fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double,
        callback: (String?) -> Unit
    ) {
        val apiKey = "99f7960af40f5028a1a7aac9376711e9"
        val url =
            "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=$longitude&y=$latitude&input_coord=WGS84"

        val request = Request.Builder().url(url).header("Authorization", "KakaoAK $apiKey").build()

        val client = OkHttpClient()
        client
            .newCall(request)
            .enqueue(
                object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body.string()
                        try {
                            val jsonObject = JSONObject(responseBody)
                            val documents = jsonObject.getJSONArray("documents")
                            if (documents.length() > 0) {
                                val firstDocument = documents.getJSONObject(0)
                                val address = firstDocument.getJSONObject("address")
                                val addressName = address.getString("address_name")
                                callback(addressName)
                            }
                        } catch (e: Exception) {
                            Log.e("ApiClient", "Error parsing JSON", e)
                            callback(null) // 예외 발생 시
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("ApiClient", "Failed to execute request", e)
                        callback(null) // 예외 발생 시
                    }
                }
            )
    }
}
