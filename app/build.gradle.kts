plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.devtools.ksp")
    alias(libs.plugins.safe.args)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt)

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.swu.myapplication"
    compileSdk = 35

    buildFeatures {
        compose  = true
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.swu.myapplication"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //by viewModels
    implementation(libs.androidx.activity.ktx)

    //coroutine
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    //viewModelScope lifecycleScope
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //dataBinding
    implementation(libs.lifecycle.compiler)

    //lottie
    implementation(libs.airbnb.lottie)

    //shapeofview
    implementation(libs.shapeofview)

    //colorpickerView
    implementation(libs.colorpickerview)

    //brv
    //implementation(libs.brv)

    //glide
    implementation(libs.glide)

    //pdfbox
    implementation(libs.pdfbox)

    //smartTable
    //implementation(libs.smarttable)

    implementation(libs.json)
    implementation(libs.okhttp)
    implementation(libs.gson)

    //所有选择器的基础窗体（用于自定义弹窗）
    implementation(libs.android.picker)
    //滚轮选择器的滚轮控件（用于自定义滚轮选择器）
    implementation(libs.android.picker.wheelview)
    //单项/数字、二三级联动、日期/时间等滚轮选择器
    implementation(libs.android.picker.wheelview.picker)

    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //implementation "androidx.activity:activity-compose:$latestVersion" // 最新稳定版
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
//    implementation "androidx.compose.animation:animation:1.8.0"
    implementation(libs.androidx.compose.animation)
//    implementation "androidx.compose.material:material:1.8.0"
    implementation(libs.androidx.compose.material)
//debugImplementation("androidx.compose.ui:ui-tooling")
    implementation(libs.androidx.compose.ui.tooling)
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}