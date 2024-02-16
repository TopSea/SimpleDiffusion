plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "top.topsea.simplediffusion"
    compileSdk = 34

    defaultConfig {
        applicationId = "top.topsea.simplediffusion"
        minSdk = 29
        targetSdk = 34
        versionCode = 8
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }


    buildTypes {
        debug {
            buildConfigField("boolean", "debug", "true")
            //为debug版本的包名添加.debug后缀
            applicationIdSuffix = ".debug"
//            manifestPlaceholders = [
//                app_id : "top.topsea.simplediffusion.debug",
//                app_icon: "@drawable/app_icon_debug",
//                app_name: "@string/app_name_debug"
//            ]
            manifestPlaceholders["app_id"] = "top.topsea.simplediffusion.debug"
            manifestPlaceholders["app_icon"] = "@drawable/app_icon_debug"
            manifestPlaceholders["app_name"] = "@string/app_name_debug"
        }
        release {
            buildConfigField("boolean", "debug", "false")

            manifestPlaceholders["app_id"] = "top.topsea.simplediffusion"
            manifestPlaceholders["app_icon"] = "@drawable/app_icon"
            manifestPlaceholders["app_name"] = "@string/app_name"

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    // 相机
    val cameraxVersion = "1.2.2"
    val navVersion = "2.5.3"
    val mmkvVersion = "1.2.16"
    val okhttpVersion = "4.11.0"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.6.1"
    val accompanistVersion = "0.31.2-alpha"
    val coilVersion = "2.4.0"

    implementation("io.coil-kt:coil-compose:$coilVersion")

    implementation("androidx.room:room-runtime:$roomVersion")
    //room 协程
    implementation("androidx.room:room-ktx:$roomVersion")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("com.tencent:mmkv:$mmkvVersion")
    implementation("androidx.compose.animation:animation:1.6.1")


    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")


    //Hilt
    val hiltVersion = "2.50"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0-alpha11")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}