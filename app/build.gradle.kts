
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jg.barcodescanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jg.barcodescanner"
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

//    barcode
    // https://mvnrepository.com/artifact/com.google.zxing/core
    implementation("com.google.zxing:core:3.5.3")
    // https://mvnrepository.com/artifact/com.journeyapps/zxing-android-embedded
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

//    api request
    // https://mvnrepository.com/artifact/com.android.volley/volley
    implementation("com.android.volley:volley:1.2.1")

    // https://mvnrepository.com/artifact/com.google.android.material/material
    runtimeOnly("com.google.android.material:material:1.12.0")

    // https://mvnrepository.com/artifact/org.simpleframework/simple-xml
    implementation("org.simpleframework:simple-xml:2.7.1")





    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}