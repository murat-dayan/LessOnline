plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.google.gms.google.services)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.muratdayan.lessonline"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.muratdayan.lessonline"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":chatbot"))
    implementation(project(":chat"))
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //HILT
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //FIREBASE
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation (libs.play.services.auth)

    //NAVIGATION
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // VIEWMODEL
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    // CAMERA
    implementation (libs.androidx.camera.core)
    implementation( libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.guava)

    //CREDENTIAL
    implementation(libs.androidx.credentials)

    //GLIDE
    implementation (libs.glide)

    //SHIMMER
    implementation (libs.shimmer)

    //LOTTIE
    implementation (libs.lottie)

    // IMAGE CROP
    implementation(libs.android.image.cropper)

    // ADMOB
    implementation (libs.play.services.ads)

    // BILLING LIBRARY
    implementation(libs.billing.ktx)

    // SPLASH SCREEN API
    implementation (libs.androidx.core.splashscreen)

    // SWIPE REFRESH LAYOUT
    implementation(libs.androidx.swiperefreshlayout)
}