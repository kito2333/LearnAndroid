package com.example.helloworld.web

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AppService {

    @GET("get_data.json")
    fun getAppData(): Call<List<App>>

    // GET http://example.com/<page>/get_data.json
    @GET("{page}/get_data.json")
    fun getPageAppData(@Path("page") page: Int): Call<List<App>>

    // GET http://example.com/get_data.json?u=<user>&t=<token>
    @GET("get_data.json")
    fun getUserAppData(@Query("u") user: String, @Query("t") token: String): Call<App>

    // DELETE http://example.com/data/<id>
    @DELETE("data/{id}")
    fun deleteData(@Path("id") id: String): Call<ResponseBody>

    // POST http://example.com/data/create
    // {"id": 1, "content": "The description for this data."}
    @POST("data/create")
    fun createData(@Body data: Data): Call<ResponseBody>

    // 静态Headers
    // GET http://example.com/get_data.json
    // User-Agent: okhttp
    // Cache-Control: max-age=0
    @Headers("User-Agent: okhttp", "Cache-Control: max-age=0")
    @GET("get_data.json")
    fun getData(): Call<Data>

    // 动态Headers
    @GET("get_data.json")
    fun getData(
        @Header("User-Agent") userAgent: String,
        @Header("Cache-Control") cacheControl: String
    ): Call<Data>
}