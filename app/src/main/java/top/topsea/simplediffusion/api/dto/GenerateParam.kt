package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.ui.scripts.Script
import top.topsea.simplediffusion.util.TextUtil
import top.topsea.simplediffusion.util.TextUtil.ControlNets2Request


@Keep
data class Txt2Img(
    var enable_hr: Boolean = false,
    var denoising_strength: Int = 0,
    var firstphase_width: Int = 0,
    var firstphase_height: Int = 0,
    var hr_scale: Int = 2,
    var hr_upscaler: String = "string",
    var hr_second_pass_steps: Int = 0,
    var hr_resize_x: Int = 0,
    var hr_resize_y: Int = 0,
    var hr_sampler_name: String = "string",
    var hr_prompt: String = "",
    var hr_negative_prompt: String = "",
    var prompt: String = "",
    val refiner_checkpoint: String = "",
    val refiner_switch_at: Float = 0f,
    var styles: Array<String> = emptyArray(),
    var seed: Int = -1,
    var subseed: Int = -1,
    var subseed_strength: Int = 0,
    var seed_resize_from_h: Int = -1,
    var seed_resize_from_w: Int = -1,
    var sampler_name: String = "",
    var batch_size: Int = 1,
    var n_iter: Int = 1,
    var steps: Int = 50,
    var cfg_scale: Float = 7f,
    var width: Int = 512,
    var height: Int = 512,
    var restore_faces: Boolean = false,
    var tiling: Boolean = false,
    var do_not_save_samples: Boolean = false,
    var do_not_save_grid: Boolean = false,
    var negative_prompt: String = "string",
    var eta: Int = 0,
    var s_min_uncond: Int = 0,
    var s_churn: Int = 0,
    var s_tmax: Int = 0,
    var s_tmin: Int = 0,
    var s_noise: Int = 1,
    var override_settings: JsonObject = JsonObject(),
    var override_settings_restore_afterwards: Boolean = true,
    val script_args: Script? = null,
//    var sampler_index: String = "Euler",
    var script_name: String = "",
    var send_images: Boolean = true,
    var save_images: Boolean = false,
    var alwayson_scripts: JsonObject = JsonObject()
) {
    override fun toString(): String {
        return "{\n" +
                "  \"enable_hr\": ${enable_hr},\n" +
                "  \"denoising_strength\": ${denoising_strength},\n" +
                "  \"firstphase_width\": ${firstphase_width},\n" +
                "  \"firstphase_height\": ${firstphase_height},\n" +
                "  \"hr_scale\": ${hr_scale},\n" +
                "  \"hr_upscaler\": \"${hr_upscaler}\",\n" +
                "  \"hr_second_pass_steps\": ${hr_second_pass_steps},\n" +
                "  \"hr_resize_x\": ${hr_resize_x},\n" +
                "  \"hr_resize_y\": ${hr_resize_y},\n" +
                "  \"hr_sampler_name\": \"${hr_sampler_name}\",\n" +
                "  \"hr_prompt\": \"${hr_prompt}\",\n" +
                "  \"hr_negative_prompt\": \"${hr_negative_prompt}\",\n" +
                "  \"prompt\": \"${prompt}\",\n" +
                "  \"styles\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"seed\": ${seed},\n" +
                "  \"subseed\": ${subseed},\n" +
                "  \"subseed_strength\": ${subseed_strength},\n" +
                "  \"seed_resize_from_h\": ${seed_resize_from_h},\n" +
                "  \"seed_resize_from_w\": ${seed_resize_from_w},\n" +
                "  \"sampler_name\": \"${sampler_name}\",\n" +
                "  \"batch_size\": ${batch_size},\n" +
                "  \"n_iter\": ${n_iter},\n" +
                "  \"steps\": ${steps},\n" +
                "  \"cfg_scale\": ${cfg_scale},\n" +
                "  \"width\": ${width},\n" +
                "  \"height\": ${height},\n" +
                "  \"restore_faces\": ${restore_faces},\n" +
                "  \"tiling\": ${tiling},\n" +
                "  \"do_not_save_samples\": ${do_not_save_samples},\n" +
                "  \"do_not_save_grid\": ${do_not_save_grid},\n" +
                "  \"negative_prompt\": \"${negative_prompt}\",\n" +
                "  \"eta\": ${eta},\n" +
                "  \"s_min_uncond\": ${s_min_uncond},\n" +
                "  \"s_churn\": ${s_churn},\n" +
                "  \"s_tmax\": ${s_tmax},\n" +
                "  \"s_tmin\": ${s_tmin},\n" +
                "  \"s_noise\": ${s_noise},\n" +
                "  \"override_settings\": {},\n" +
                "  \"override_settings_restore_afterwards\": ${override_settings_restore_afterwards},\n" +
                "  \"script_args\": [],\n" +
//                "  \"sampler_index\": \"${sampler_index}\",\n" +
                "  \"script_name\": \"${script_name}\",\n" +
                "  \"send_images\": ${send_images},\n" +
                "  \"save_images\": ${save_images},\n" +
                "  \"alwayson_scripts\": {}\n" +
                "}"
    }

    fun requestWithCN(cnModels: List<CNParam>): String {
        // 脚本
        val scriptName = if (script_name.isEmpty() || script_name == "None")
            ""
        else
            "  \"script_name\": \"$script_name\",\n"
        val scriptArgs = if (script_name.isNotEmpty() && script_args != null)
            "  \"script_args\": ${TextUtil.script2String(script_args)},\n"
        else
            ""

        val controlNet = if (cnModels.isNotEmpty())
            "\"ControlNet\": {\n" +
                    "   \"args\": [" +
                    ControlNets2Request(cnModels = cnModels) +
                    "]\n" +
                    "}\n"
        else
            ""

        return "{\n" +
                "  \"enable_hr\": ${enable_hr},\n" +
                "  \"denoising_strength\": ${denoising_strength},\n" +
                "  \"firstphase_width\": ${firstphase_width},\n" +
                "  \"firstphase_height\": ${firstphase_height},\n" +
                "  \"hr_scale\": ${hr_scale},\n" +
                "  \"hr_upscaler\": \"${hr_upscaler}\",\n" +
                "  \"hr_second_pass_steps\": ${hr_second_pass_steps},\n" +
                "  \"hr_resize_x\": ${hr_resize_x},\n" +
                "  \"hr_resize_y\": ${hr_resize_y},\n" +
                "  \"hr_sampler_name\": \"${hr_sampler_name}\",\n" +
                "  \"hr_prompt\": \"${hr_prompt}\",\n" +
                "  \"hr_negative_prompt\": \"${hr_negative_prompt}\",\n" +
                "  \"prompt\": \"${prompt}\",\n" +
                "  \"refiner_checkpoint\" : \"${refiner_checkpoint}\",\n" +
                "  \"refiner_switch_at\" : ${refiner_switch_at},\n" +
                "  \"styles\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"seed\": ${seed},\n" +
                "  \"subseed\": ${subseed},\n" +
                "  \"subseed_strength\": ${subseed_strength},\n" +
                "  \"seed_resize_from_h\": ${seed_resize_from_h},\n" +
                "  \"seed_resize_from_w\": ${seed_resize_from_w},\n" +
                "  \"sampler_name\": \"${sampler_name}\",\n" +
                "  \"batch_size\": ${batch_size},\n" +
                "  \"n_iter\": ${n_iter},\n" +
                "  \"steps\": ${steps},\n" +
                "  \"cfg_scale\": ${cfg_scale},\n" +
                "  \"width\": ${width},\n" +
                "  \"height\": ${height},\n" +
                "  \"restore_faces\": ${restore_faces},\n" +
                "  \"tiling\": ${tiling},\n" +
                "  \"do_not_save_samples\": ${do_not_save_samples},\n" +
                "  \"do_not_save_grid\": ${do_not_save_grid},\n" +
                "  \"negative_prompt\": \"${negative_prompt}\",\n" +
                "  \"eta\": ${eta},\n" +
                "  \"s_min_uncond\": ${s_min_uncond},\n" +
                "  \"s_churn\": ${s_churn},\n" +
                "  \"s_tmax\": ${s_tmax},\n" +
                "  \"s_tmin\": ${s_tmin},\n" +
                "  \"s_noise\": ${s_noise},\n" +
                "  \"override_settings\": {},\n" +
                "  \"override_settings_restore_afterwards\": ${override_settings_restore_afterwards},\n" +
//                "  \"sampler_index\": \"${sampler_index}\",\n" +
                scriptName +
                scriptArgs +
                "  \"send_images\": ${send_images},\n" +
                "  \"save_images\": ${save_images},\n" +
                "  \"alwayson_scripts\" : {\n" +
                        controlNet +
                "   }\n"+
                "}"
    }
}

@Keep
data class Img2Img(
    var init_images: Array<String> = emptyArray(),
    var resize_mode: Int = 0,
    var denoising_strength: Float = 0.05f,
    var image_cfg_scale: Int = 0,
    var mask: String = "string",
    var mask_blur: Int = 0,
    var mask_blur_x: Int = 4,
    var mask_blur_y: Int = 4,
    var inpainting_fill: Int = 0,
    var inpaint_full_res: Boolean = true,
    var inpaint_full_res_padding: Int = 32,
    var inpainting_mask_invert: Int = 1,
    var initial_noise_multiplier: Int = 0,
    var prompt: String = "1girl",
    val refiner_checkpoint: String = "",
    val refiner_switch_at: Float = 0f,
    var styles: Array<String> = emptyArray(),
    var seed: Int = -1,
    var subseed: Int = -1,
    var subseed_strength: Int = 0,
    var seed_resize_from_h: Int = -1,
    var seed_resize_from_w: Int = -1,
    var sampler_name: String = "string",
    var batch_size: Int = 1,
    var n_iter: Int = 1,
    var steps: Int = 50,
    var cfg_scale: Float = 7f,
    var width: Int = 512,
    var height: Int = 512,
    var restore_faces: Boolean = false,
    var tiling: Boolean = false,
    var do_not_save_samples: Boolean = false,
    var do_not_save_grid: Boolean = false,
    var negative_prompt: String = "string",
    var eta: Int = 0,
    var s_min_uncond: Int = 0,
    var s_churn: Int = 0,
    var s_tmax: Int = 0,
    var s_tmin: Int = 0,
    var s_noise: Int = 1,
    var override_settings: JsonObject = JsonObject(),
    var override_settings_restore_afterwards: Boolean = true,
    var script_args: Script? = null,
//    var sampler_index: String = "Euler",
    var include_init_images: Boolean = false,
    var script_name: String = "",
    var send_images: Boolean = true,
    var save_images: Boolean = false,
    var alwayson_scripts: JsonObject = JsonObject()
) {
    override fun toString(): String {
        return "{\n" +
                "   \"init_images\" : ${init_images.isNotEmpty()},\n" +
                "   \"resize_mode\" : ${resize_mode},\n" +
                "   \"denoising_strength\" : ${denoising_strength},\n" +
                "   \"image_cfg_scale\" : ${image_cfg_scale},\n" +
//                "   \"mask\" : \"${mask}\",\n" +
                "   \"mask_blur\" : ${mask_blur},\n" +
                "   \"mask_blur_x\" : ${mask_blur_x},\n" +
                "   \"mask_blur_y\" : ${mask_blur_y},\n" +
                "   \"inpainting_fill\" : ${inpainting_fill},\n" +
                "   \"inpaint_full_res\" : ${inpaint_full_res},\n" +
                "   \"inpaint_full_res_padding\" : ${inpaint_full_res_padding},\n" +
                "   \"inpainting_mask_invert\" : ${inpainting_mask_invert},\n" +
                "   \"initial_noise_multiplier\" : ${initial_noise_multiplier},\n" +
                "   \"prompt\" : \"${prompt}\",\n" +
//                "   \"styles\" : ${styles},\n" +
                "   \"seed\" : ${seed},\n" +
                "   \"subseed\" : ${subseed},\n" +
                "   \"subseed_strength\" : ${subseed_strength},\n" +
                "   \"seed_resize_from_h\" : ${seed_resize_from_h},\n" +
                "   \"seed_resize_from_w\" : ${seed_resize_from_w},\n" +
//                "   \"sampler_name\" : \"${sampler_name}\",\n" +
                "   \"batch_size\" : ${batch_size},\n" +
                "   \"n_iter\" : ${n_iter},\n" +
                "   \"steps\" : ${steps},\n" +
                "   \"cfg_scale\" : ${cfg_scale},\n" +
                "   \"width\" : ${width},\n" +
                "   \"height\" : ${height},\n" +
                "   \"restore_faces\" : ${restore_faces},\n" +
                "   \"tiling\" : ${tiling},\n" +
                "   \"do_not_save_samples\" : ${do_not_save_samples},\n" +
                "   \"do_not_save_grid\" : ${do_not_save_grid},\n" +
                "   \"negative_prompt\" : \"${negative_prompt}\",\n" +
                "   \"eta\" :${eta},\n" +
                "   \"s_min_uncond\" : ${s_min_uncond},\n" +
                "   \"s_churn\" : ${s_churn},\n" +
                "   \"s_tmax\" : ${s_tmax},\n" +
                "   \"s_tmin\" : ${s_tmin},\n" +
                "   \"s_noise\" : ${s_noise},\n" +
//                "   \"override_settings\" : ${override_settings},\n" +
                "   \"override_settings_restore_afterwards\" : ${override_settings_restore_afterwards},\n" +
//                "   \"script_args\" : ${script_args},\n" +
//                "   \"sampler_index\" : \"${sampler_index}\",\n" +
                "   \"include_init_images\" : ${include_init_images},\n" +
//                "   \"script_name\" : \"${script_name}\",\n" +
                "   \"send_images\" : ${send_images},\n" +
                "   \"save_images\" : ${save_images}\n" +
//                "   \"alwayson_scripts\" : ${alwayson_scripts}" +
                "}"
    }

    fun requestWithCN(cnModels: List<CNParam>): String {
        // 脚本
        val scriptName = if (script_name.isEmpty() || script_name == "None")
            ""
        else
            "  \"script_name\": \"$script_name\",\n"
        val scriptArgs = if (script_name.isNotEmpty() && script_args != null)
            "  \"script_args\": ${TextUtil.script2String(script_args)},\n"
        else
            ""

        val controlNet = if (cnModels.isNotEmpty())
            "\"ControlNet\": {\n" +
                    "   \"args\": [" +
                    ControlNets2Request(cnModels = cnModels) +
                    "]\n" +
                    "}\n"
        else
            ""

        return "{\n" +
                "   \"init_images\" : ${TextUtil.as2String(init_images)},\n" +
                "   \"resize_mode\" : ${resize_mode},\n" +
                "   \"denoising_strength\" : ${denoising_strength},\n" +
                "   \"image_cfg_scale\" : ${image_cfg_scale},\n" +
//                "   \"mask\" : \"${mask}\",\n" +
//                "   \"mask_blur\" : ${mask_blur},\n" +
//                "   \"mask_blur_x\" : ${mask_blur_x},\n" +
//                "   \"mask_blur_y\" : ${mask_blur_y},\n" +
//                "   \"inpainting_fill\" : ${inpainting_fill},\n" +
//                "   \"inpaint_full_res\" : ${inpaint_full_res},\n" +
//                "   \"inpaint_full_res_padding\" : ${inpaint_full_res_padding},\n" +
//                "   \"inpainting_mask_invert\" : ${inpainting_mask_invert},\n" +
//                "   \"initial_noise_multiplier\" : ${initial_noise_multiplier},\n" +
                "   \"prompt\" : \"${prompt}\",\n" +
                "   \"refiner_checkpoint\" : \"${refiner_checkpoint}\",\n" +
                "   \"refiner_switch_at\" : ${refiner_switch_at},\n" +
//                "   \"styles\" : ${styles},\n" +
                "   \"seed\" : ${seed},\n" +
//                "   \"subseed\" : ${subseed},\n" +
//                "   \"subseed_strength\" : ${subseed_strength},\n" +
//                "   \"seed_resize_from_h\" : ${seed_resize_from_h},\n" +
//                "   \"seed_resize_from_w\" : ${seed_resize_from_w},\n" +
                "   \"sampler_name\" : \"${sampler_name}\",\n" +
                "   \"batch_size\" : ${batch_size},\n" +
//                "   \"n_iter\" : ${n_iter},\n" +
                "   \"steps\" : ${steps},\n" +
                "   \"cfg_scale\" : ${cfg_scale},\n" +
                "   \"width\" : ${width},\n" +
                "   \"height\" : ${height},\n" +
//                "   \"restore_faces\" : ${restore_faces},\n" +
//                "   \"tiling\" : ${tiling},\n" +
//                "   \"do_not_save_samples\" : ${do_not_save_samples},\n" +
//                "   \"do_not_save_grid\" : ${do_not_save_grid},\n" +
                "   \"negative_prompt\" : \"${negative_prompt}\",\n" +
//                "   \"eta\" :${eta},\n" +
//                "   \"s_min_uncond\" : ${s_min_uncond},\n" +
//                "   \"s_churn\" : ${s_churn},\n" +
//                "   \"s_tmax\" : ${s_tmax},\n" +
//                "   \"s_tmin\" : ${s_tmin},\n" +
//                "   \"s_noise\" : ${s_noise},\n" +
//                "   \"override_settings\" : ${override_settings},\n" +
//                "   \"override_settings_restore_afterwards\" : ${override_settings_restore_afterwards},\n" +
//                "   \"sampler_index\" : \"${sampler_index}\",\n" +
                "   \"include_init_images\" : ${include_init_images},\n" +
                scriptName +
                scriptArgs +
                "   \"send_images\" : ${send_images},\n" +
                "   \"save_images\" : ${save_images},\n" +
                "   \"alwayson_scripts\" : {\n" +
                        controlNet +
                "   }\n"+
                "}"
    }
}
