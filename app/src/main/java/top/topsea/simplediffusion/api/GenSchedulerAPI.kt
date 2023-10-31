package top.topsea.simplediffusion.api

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface GenSchedulerAPI {
    @Headers("Accept: application/json")
    @GET("/agent-scheduler/v1/queue")
    fun getQueue(): Call<String>

    @Headers("Accept: application/json")
    @POST("/agent-scheduler/v1/history")
    fun getHistory(): Call<String>
}