package top.topsea.simplediffusion.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface PromptApi {
    @Headers("Accept: application/json")
    @GET("/old_six_prompt/version")
    fun getPromptVersion(): Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("/old_six_prompt/all_prompts")
    fun getAllPrompts(): Call<ResponseBody>
}