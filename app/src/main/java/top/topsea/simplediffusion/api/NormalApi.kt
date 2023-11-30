package top.topsea.simplediffusion.api

import com.google.gson.JsonArray
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import top.topsea.simplediffusion.api.dto.CNSettings
import top.topsea.simplediffusion.api.dto.CNVersion
import top.topsea.simplediffusion.api.dto.GenerateProgress
import top.topsea.simplediffusion.api.dto.TaskProgress
import top.topsea.simplediffusion.api.dto.Types

interface NormalApi {
    @Headers("Accept: application/json")
    @GET("/sdapi/v1/sd-models")
    fun getSdModels(): Call<JsonArray>

    @Headers("Accept: application/json")
    @GET("/sdapi/v1/samplers")
    fun getSDSamplers(): Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("/sdapi/v1/prompt-styles")
    fun getSDPrompts(): Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("/controlnet/control_types")
    fun getCNTypes(): Call<Types>

    @Headers("Accept: application/json")
    @GET("/controlnet/settings")
    fun getCNSettings(): Call<CNSettings>

    @Headers("Accept: application/json")
    @GET("/controlnet/version")
    fun getCNVersion(): Call<CNVersion>

    @Headers("Accept: application/json")
    @POST("/sdapi/v1/skip")
    fun skipGenerate(): Call<ResponseBody>

    @Headers("Accept: application/json")
    @DELETE("/agent-scheduler/v1/task/{taskID}")
    fun deleteAgentTask(@Path("taskID") taskID: String): Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("/sdapi/v1/sd-vae")
    fun getVaes(): Call<JsonArray>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/sdapi/v1/progress")
    fun generatingProgress(): Call<GenerateProgress>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/internal/progress")
    fun taskProgress(@Body json: RequestBody): Call<TaskProgress>

    @Headers("Accept: application/json")
    @GET("/sdapi/v1/loras")
    fun getLoras(): Call<JsonArray>

    @Headers("Accept: application/json")
    @POST("/sdapi/v1/options")
    fun checkSDConnect(@Body json: RequestBody): Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("/agent-scheduler/v1/queue")
    fun checkAgentScheduler(): Call<ResponseBody>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sdapi/v1/options")
    fun updateSdConfig(@Body json: RequestBody): Call<ResponseBody>
}