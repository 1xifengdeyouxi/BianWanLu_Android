// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    alias(libs.plugins.safe.args) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.compose.compiler) apply false
}