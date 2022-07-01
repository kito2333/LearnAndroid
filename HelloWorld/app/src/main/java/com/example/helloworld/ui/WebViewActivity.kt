package com.example.helloworld.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebViewClient
import com.example.helloworld.R
import com.example.helloworld.web.App
import com.example.helloworld.web.AppService
import com.example.helloworld.web.HttpUtil
import com.example.helloworld.web.JsonDataGsonParser
import kotlinx.android.synthetic.main.activity_web_view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.text.StringBuilder

class WebViewActivity : AppCompatActivity() {
    companion object {
        const val TAG = "WebViewActivity"
        const val BASE_URL = "http://10.23.180.246"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
//        initWebView()
        sendRequestBtn.setOnClickListener {
            mockOkHttpRequest()
        }
        getAppDataBtn.setOnClickListener {
            val appService = HttpUtil.create<AppService>()
            appService.getAppData().enqueue(object : Callback<List<App>> {
                override fun onResponse(call: Call<List<App>>, response: Response<List<App>>) {
                    val list = response.body()
                    if (list != null) {
                        for (app in list) {
                            Log.d(
                                TAG,
                                "id is ${app.id}, name is ${app.name}, version is ${app.version}"
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<List<App>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val url = intent.getStringExtra("url") ?: return
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
        webView.visibility = View.VISIBLE
    }

    private fun mockHttpRequest() {
        thread {
            var connection: HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL("https://www.baidu.com")
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                }
                val input = connection.inputStream
                // 对于输入流进行读取
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                showResponse(response.toString())
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }

    }

    // GET
    private fun mockOkHttpRequest() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.23.180.246/get_data.json")
                    .build()
                val response = client.newCall(request).execute()
                response.body?.string()?.let {
                    JsonDataGsonParser().parse(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // POST
    private fun mockOkHttpPostRequest() {
        thread {
            try {
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("username", "admin")
                    .add("password", "123456")
                    .build()
                val request = Request.Builder()
                    .url("https://www.baidu.com")
                    .post(requestBody)
                    .build()
                val response = client.newCall(request).execute()
                response.body?.string()?.let {
                    showResponse(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showResponse(resp: String) {
        runOnUiThread {
            responseText.text = resp
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        compatImmersiveMode()
    }

    // sdk >= 21
    private fun compatImmersiveMode() {
        val decorView = window.decorView
        // 透明状态栏, 隐藏需要用SYSTEM_UI_FLAG_FULLSCREEN
//        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//        decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 适配小米隐藏状态栏后的黑边
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }


        // 隐藏ActionBar
        supportActionBar?.hide()

        // 隐藏导航栏和状态栏
//        decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        // 沉浸模式
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

    }
}