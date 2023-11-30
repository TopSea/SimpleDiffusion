package top.topsea.simplediffusion.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming
import top.topsea.simplediffusion.api.dto.ImgResponse
import top.topsea.simplediffusion.api.dto.QueueTask

interface GenImgApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sdapi/v1/txt2img")
    fun txt2Img(@Body json: RequestBody): Call<ImgResponse>

    @Streaming
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sdapi/v1/img2img")
    fun img2Img(@Body json: RequestBody): Call<ImgResponse>

    @Headers("Accept: application/json", "Accept: application/json")
    @POST("/agent-scheduler/v1/queue/txt2img")
    fun queueTxt2Img(@Body json: RequestBody): Call<QueueTask>

    @Headers("Accept: application/json", "Accept: application/json")
    @POST("/agent-scheduler/v1/queue/img2img")
    fun queueImg2Img(@Body json: RequestBody): Call<QueueTask>

    @Headers("Accept: application/json", "Accept: application/json")
    @GET("/agent-scheduler/v1/task/{taskID}/results")
    fun dQueueResults(@Path("taskID") taskID: String): Call<ResponseBody>
}