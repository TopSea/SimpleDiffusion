package top.topsea.simplediffusion.ui.component

import android.content.Context
import top.topsea.simplediffusion.R

fun getXYZType(context: Context): List<Pair<String, String>> {
    return listOf(
            "Nothing" to context.getString(R.string.app_name),
            "Seed" to context.getString(R.string.app_name),
            "Var.seed" to context.getString(R.string.app_name),
            "Var.strength" to context.getString(R.string.app_name),
            "Steps" to context.getString(R.string.app_name),
            "Hires steps" to context.getString(R.string.app_name),
            "CFG Scale" to context.getString(R.string.app_name),
            "Prompt S/R" to context.getString(R.string.app_name),
            "Prompt order" to context.getString(R.string.app_name),
            "Sampler" to context.getString(R.string.app_name),
            "Checkpoint name" to context.getString(R.string.app_name),
            "Negative Guidance minimum sigma" to context.getString(R.string.app_name),
            "Sigma Churn" to context.getString(R.string.app_name),
            "Sigma min" to context.getString(R.string.app_name),
            "Sigma max" to context.getString(R.string.app_name),
            "Sigma noise" to context.getString(R.string.app_name),
            "Schedule min sigma" to context.getString(R.string.app_name),
            "Schedule max sigma" to context.getString(R.string.app_name),
            "Schedule rho" to context.getString(R.string.app_name),
            "Eta" to context.getString(R.string.app_name),
            "Clip skip" to context.getString(R.string.app_name),
            "Denoising" to context.getString(R.string.app_name),
            "Hires upscaler" to context.getString(R.string.app_name),
            "VAE" to context.getString(R.string.app_name),
            "Styles" to context.getString(R.string.app_name),
            "UniPC Order" to context.getString(R.string.app_name),
            "Face restore" to context.getString(R.string.app_name),
            "Token merging ratio" to context.getString(R.string.app_name),
            "Token merging ratio high-res" to context.getString(R.string.app_name),
            "[ControlNet] Enabled" to context.getString(R.string.app_name),
            "[ControlNet] Model" to context.getString(R.string.app_name),
            "[ControlNet] Weight" to context.getString(R.string.app_name),
            "[ControlNet] Guidance Start" to context.getString(R.string.app_name),
            "[ControlNet] Guidance End" to context.getString(R.string.app_name),
            "[ControlNet] Resize Mode" to context.getString(R.string.app_name),
            "[ControlNet] Preprocessor" to context.getString(R.string.app_name),
            "[ControlNet] Pre Resolution" to context.getString(R.string.app_name),
            "[ControlNet] Pre Threshold A" to context.getString(R.string.app_name),
            "[ControlNet] Pre Threshold B" to context.getString(R.string.app_name),
        )

}
