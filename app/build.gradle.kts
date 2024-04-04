plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.wardani"
    compileSdk = 34
    flavorDimensions("environment")

    defaultConfig {
        applicationId = "com.example.wardani"
        minSdk = 21
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

    productFlavors {
        create("sandbox") {
            buildConfigField("String", "BASE_URL", "\"https://wardani-midtrans.vercel.app//api/\"")
            buildConfigField("String", "CLIENT_KEY", "\"SB-Mid-client-VSmpsstbFY_OG2-y\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //SlideShowImage
    implementation("com.github.denzcoskun:ImageSlideShow:0.1.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // For using the Midtrans Sandbox
    implementation("com.midtrans:uikit:2.0.0-SANDBOX")
    implementation("com.midtrans:uikit:1.29.0")
}