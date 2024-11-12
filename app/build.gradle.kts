plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.almi.juegaalmiapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.almi.juegaalmiapp"
        minSdk = 24
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
}

dependencies {

    implementation (libs.gson)
    implementation (libs.okhttp3.okhttp)
    implementation (libs.logging.interceptor)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.com.android.volley.volley)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.google.android.maps.utils)
    implementation (libs.maps.google.maps.services)
    implementation (libs.play.services.location)
    implementation (libs.androidx.cardview)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material.v190)
}