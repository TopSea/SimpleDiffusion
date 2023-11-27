package top.topsea.simplediffusion.util

object Constant {
    // AddablePrompt 格式
    val addablePattern by lazy {
        Regex("""(罒)([^罓]*)(罓)""")
    }
    val addableFirst by lazy {
        "罒"
    }
    val addableSecond by lazy {
        "罓"
    }
    const val date_format = "yyyy-MM-dd HH:mm:ss"
    const val default_date = "1997-01-01 00:00:00"
    const val download_buffer = 8192

    const val k_delete_mode = "delete_mode"
    const val k_use_mode = "Use_Mode"
    const val k_save_cap_img = "save_cap_img"
    const val k_save_grid_img = "k_save_grid_img"
    const val k_save_control = "save_control"
    const val k_save_on_server = "save_on_server"
    const val k_show_gen_on_1 = "show_gen_on_1"
    const val k_ex_agent_scheduler = "ex_gen_scheduler"
    const val k_ex_sd_prompt = "ex_sd_prompt"

    const val k_t_display_pri = "k_t_display_pri"
    const val k_t_display_pri_s = "k_t_display_pri_s"
    const val k_t_sdmodel = "k_t_sdmodel"
    const val k_t_sdmodel_s = "k_t_sdmodel_s"
    const val k_t_refinermodel = "k_t_refinermodel"
    const val k_t_refinermodel_s = "k_t_refinermodel_s"
    const val k_t_refinerat = "k_t_refinerat"
    const val k_t_refinerat_s = "k_t_refinerat_s"
    const val k_t_prompt = "k_t_prompt"
    const val k_t_prompt_s = "k_t_prompt_s"
    const val k_t_promptad_s = "k_t_promptad_s"
    const val k_t_nprompt = "k_t_nprompt"
    const val k_t_nprompt_s = "k_t_nprompt_s"
    const val k_t_imgw = "k_t_imgw"
    const val k_t_imgw_s = "k_t_imgw_s"
    const val k_t_imgh = "k_t_imgh"
    const val k_t_imgh_s = "k_t_imgh_s"
    const val k_t_step = "k_t_step"
    const val k_t_step_s = "k_t_step_s"
    const val k_t_cfg = "k_t_cfg"
    const val k_t_cfg_s = "k_t_cfg_s"
    const val k_t_sampler = "k_t_sampler"
    const val k_t_sampler_s = "k_t_sampler_s"
    const val k_t_bsize = "k_t_bsize"
    const val k_t_bsize_s = "k_t_bsize_s"
    const val k_t_sdprompt_s = "k_t_sdprompt_s"
    const val k_t_script_s = "k_t_script_s"
    const val k_t_cn_s = "k_t_cn_s"

    var TAG_RECEIPT_HEADER = "\${HZ}$:"
    var SERVER_PORT = 10086
    var TIME_OUT_SEND: Long = 30000
    var TIME_OUT_LINK: Long = 30000
    var MAX_BACKLOG = 50


    // Stable Diffusion WebUI 设置
    const val samples_save = "samples_save"
    const val samples_format = "samples_format"
    const val samples_filename_pattern = "samples_filename_pattern"
    const val save_images_add_number = "save_images_add_number"
    const val grid_save = "grid_save"
    const val grid_format = "grid_format"
    const val grid_extended_filename = "grid_extended_filename"
    const val grid_only_if_multiple = "grid_only_if_multiple"
    const val grid_prevent_empty_spots = "grid_prevent_empty_spots"
    const val grid_zip_filename_pattern = "grid_zip_filename_pattern"
    const val n_rows = "n_rows"
    const val enable_pnginfo = "enable_pnginfo"
    const val save_txt = "save_txt"
    const val save_images_before_face_restoration = "save_images_before_face_restoration"
    const val save_images_before_highres_fix = "save_images_before_highres_fix"
    const val save_images_before_color_correction = "save_images_before_color_correction"
    const val save_mask = "save_mask"
    const val save_mask_composite = "save_mask_composite"
    const val jpeg_quality = "jpeg_quality"
    const val webp_lossless = "webp_lossless"
    const val export_for_4chan = "export_for_4chan"
    const val img_downscale_threshold = "img_downscale_threshold"
    const val target_side_length = "target_side_length"
    const val img_max_size_mp = "img_max_size_mp"
    const val use_original_name_batch = "use_original_name_batch"
    const val use_upscaler_name_as_suffix = "use_upscaler_name_as_suffix"
    const val save_selected_only = "save_selected_only"
    const val save_init_img = "save_init_img"
    const val temp_dir = "temp_dir"
    const val clean_temp_dir_at_start = "clean_temp_dir_at_start"
    const val outdir_samples = "outdir_samples"
    const val outdir_txt2img_samples = "outdir_txt2img_samples"
    const val outdir_img2img_samples = "outdir_img2img_samples"
    const val outdir_extras_samples = "outdir_extras_samples"
    const val outdir_grids = "outdir_grids"
    const val outdir_txt2img_grids = "outdir_txt2img_grids"
    const val outdir_img2img_grids = "outdir_img2img_grids"
    const val outdir_save = "outdir_save"
    const val outdir_init_images = "outdir_init_images"
    const val save_to_dirs = "save_to_dirs"
    const val grid_save_to_dirs = "grid_save_to_dirs"
    const val use_save_to_dirs_for_ui = "use_save_to_dirs_for_ui"
    const val directories_filename_pattern = "directories_filename_pattern"
    const val directories_max_prompt_words = "directories_max_prompt_words"
    const val ESRGAN_tile = "ESRGAN_tile"
    const val ESRGAN_tile_overlap = "ESRGAN_tile_overlap"
    const val realesrgan_enabled_models = "realesrgan_enabled_models"
    const val upscaler_for_img2img = "upscaler_for_img2img"
    const val face_restoration_model = "face_restoration_model"
    const val code_former_weight = "code_former_weight"
    const val face_restoration_unload = "face_restoration_unload"
    const val show_warnings = "show_warnings"
    const val memmon_poll_rate = "memmon_poll_rate"
    const val samples_log_stdout = "samples_log_stdout"
    const val multiple_tqdm = "multiple_tqdm"
    const val print_hypernet_extra = "print_hypernet_extra"
    const val list_hidden_files = "list_hidden_files"
    const val unload_models_when_training = "unload_models_when_training"
    const val pin_memory = "pin_memory"
    const val save_optimizer_state = "save_optimizer_state"
    const val save_training_settings_to_txt = "save_training_settings_to_txt"
    const val dataset_filename_word_regex = "dataset_filename_word_regex"
    const val dataset_filename_join_string = "dataset_filename_join_string"
    const val training_image_repeats_per_epoch = "training_image_repeats_per_epoch"
    const val training_write_csv_every = "training_write_csv_every"
    const val training_xattention_optimizations = "training_xattention_optimizations"
    const val training_enable_tensorboard = "training_enable_tensorboard"
    const val training_tensorboard_save_images = "training_tensorboard_save_images"
    const val training_tensorboard_flush_every = "training_tensorboard_flush_every"
    const val sd_model_checkpoint = "sd_model_checkpoint"
    const val sd_checkpoint_cache = "sd_checkpoint_cache"
    const val sd_vae_checkpoint_cache = "sd_vae_checkpoint_cache"
    const val sd_vae = "sd_vae"
    const val sd_vae_as_default = "sd_vae_as_default"
    const val sd_unet = "sd_unet"
    const val inpainting_mask_weight = "inpainting_mask_weight"
    const val initial_noise_multiplier = "initial_noise_multiplier"
    const val img2img_color_correction = "img2img_color_correction"
    const val img2img_fix_steps = "img2img_fix_steps"
    const val img2img_background_color = "img2img_background_color"
    const val enable_quantization = "enable_quantization"
    const val enable_emphasis = "enable_emphasis"
    const val enable_batch_seeds = "enable_batch_seeds"
    const val comma_padding_backtrack = "comma_padding_backtrack"
    const val CLIP_stop_at_last_layers = "CLIP_stop_at_last_layers"
    const val upcast_attn = "upcast_attn"
    const val randn_source = "randn_source"
    const val cross_attention_optimization = "cross_attention_optimization"
    const val s_min_uncond = "s_min_uncond"
    const val token_merging_ratio = "token_merging_ratio"
    const val token_merging_ratio_img2img = "token_merging_ratio_img2img"
    const val token_merging_ratio_hr = "token_merging_ratio_hr"
    const val pad_cond_uncond = "pad_cond_uncond"
    const val experimental_persistent_cond_cache = "experimental_persistent_cond_cache"
    const val use_old_emphasis_implementation = "use_old_emphasis_implementation"
    const val use_old_karras_scheduler_sigmas = "use_old_karras_scheduler_sigmas"
    const val no_dpmpp_sde_batch_determinism = "no_dpmpp_sde_batch_determinism"
    const val use_old_hires_fix_width_height = "use_old_hires_fix_width_height"
    const val dont_fix_second_order_samplers_schedule = "dont_fix_second_order_samplers_schedule"
    const val hires_fix_use_firstpass_conds = "hires_fix_use_firstpass_conds"
    const val interrogate_keep_models_in_memory = "interrogate_keep_models_in_memory"
    const val interrogate_return_ranks = "interrogate_return_ranks"
    const val interrogate_clip_num_beams = "interrogate_clip_num_beams"
    const val interrogate_clip_min_length = "interrogate_clip_min_length"
    const val interrogate_clip_max_length = "interrogate_clip_max_length"
    const val interrogate_clip_dict_limit = "interrogate_clip_dict_limit"
    const val interrogate_clip_skip_categories = "interrogate_clip_skip_categories"
    const val interrogate_deepbooru_score_threshold = "interrogate_deepbooru_score_threshold"
    const val deepbooru_sort_alpha = "deepbooru_sort_alpha"
    const val deepbooru_use_spaces = "deepbooru_use_spaces"
    const val deepbooru_escape = "deepbooru_escape"
    const val deepbooru_filter_tags = "deepbooru_filter_tags"
    const val extra_networks_show_hidden_directories = "extra_networks_show_hidden_directories"
    const val extra_networks_hidden_models = "extra_networks_hidden_models"
    const val extra_networks_default_view = "extra_networks_default_view"
    const val extra_networks_default_multiplier = "extra_networks_default_multiplier"
    const val extra_networks_card_width = "extra_networks_card_width"
    const val extra_networks_card_height = "extra_networks_card_height"
    const val extra_networks_add_text_separator = "extra_networks_add_text_separator"
    const val ui_extra_networks_tab_reorder = "ui_extra_networks_tab_reorder"
    const val sd_hypernetwork = "sd_hypernetwork"
    const val localization = "localization"
    const val gradio_theme = "gradio_theme"
    const val img2img_editor_height = "img2img_editor_height"
    const val return_grid = "return_grid"
    const val return_mask = "return_mask"
    const val return_mask_composite = "return_mask_composite"
    const val do_not_show_images = "do_not_show_images"
    const val send_seed = "send_seed"
    const val send_size = "send_size"
    const val font = "font"
    const val js_modal_lightbox = "js_modal_lightbox"
    const val js_modal_lightbox_initially_zoomed = "js_modal_lightbox_initially_zoomed"
    const val js_modal_lightbox_gamepad = "js_modal_lightbox_gamepad"
    const val js_modal_lightbox_gamepad_repeat = "js_modal_lightbox_gamepad_repeat"
    const val show_progress_in_title = "show_progress_in_title"
    const val samplers_in_dropdown = "samplers_in_dropdown"
    const val dimensions_and_batch_together = "dimensions_and_batch_together"
    const val keyedit_precision_attention = "keyedit_precision_attention"
    const val keyedit_precision_extra = "keyedit_precision_extra"
    const val keyedit_delimiters = "keyedit_delimiters"
    const val quicksettings_list = "quicksettings_list"
    const val ui_tab_order = "ui_tab_order"
    const val hidden_tabs = "hidden_tabs"
    const val ui_reorder_list = "ui_reorder_list"
    const val hires_fix_show_sampler = "hires_fix_show_sampler"
    const val hires_fix_show_prompts = "hires_fix_show_prompts"
    const val disable_token_counters = "disable_token_counters"
    const val add_model_hash_to_info = "add_model_hash_to_info"
    const val add_model_name_to_info = "add_model_name_to_info"
    const val add_version_to_infotext = "add_version_to_infotext"
    const val disable_weights_auto_swap = "disable_weights_auto_swap"
    const val infotext_styles = "infotext_styles"
    const val show_progressbar = "show_progressbar"
    const val live_previews_enable = "live_previews_enable"
    const val live_previews_image_format = "live_previews_image_format"
    const val show_progress_grid = "show_progress_grid"
    const val show_progress_every_n_steps = "show_progress_every_n_steps"
    const val show_progress_type = "show_progress_type"
    const val live_preview_content = "live_preview_content"
    const val live_preview_refresh_period = "live_preview_refresh_period"
    const val hide_samplers = "hide_samplers"
    const val eta_ddim = "eta_ddim"
    const val eta_ancestral = "eta_ancestral"
    const val ddim_discretize = "ddim_discretize"
    const val s_churn = "s_churn"
    const val s_tmin = "s_tmin"
    const val s_noise = "s_noise"
    const val k_sched_type = "k_sched_type"
    const val sigma_min = "sigma_min"
    const val sigma_max = "sigma_max"
    const val rho = "rho"
    const val eta_noise_seed_delta = "eta_noise_seed_delta"
    const val always_discard_next_to_last_sigma = "always_discard_next_to_last_sigma"
    const val uni_pc_variant = "uni_pc_variant"
    const val uni_pc_skip_type = "uni_pc_skip_type"
    const val uni_pc_order = "uni_pc_order"
    const val uni_pc_lower_order_final = "uni_pc_lower_order_final"
    const val postprocessing_enable_in_main_ui = "postprocessing_enable_in_main_ui"
    const val postprocessing_operation_order = "postprocessing_operation_order"
    const val upscaling_max_images_in_cache = "upscaling_max_images_in_cache"
    const val disabled_extensions = "disabled_extensions"
    const val disable_all_extensions = "disable_all_extensions"
    const val restore_config_state_file = "restore_config_state_file"
    const val sd_checkpoint_hash = "sd_checkpoint_hash"
    const val sd_lyco = "sd_lyco"
}