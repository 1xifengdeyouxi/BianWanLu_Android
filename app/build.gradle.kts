plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.devtools.ksp")
    alias(libs.plugins.safe.args)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.swu.myapplication"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.swu.myapplication"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
}