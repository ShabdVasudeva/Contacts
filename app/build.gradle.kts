
plugins {
    id("com.android.application")
    
}

android {
    namespace = "org.codeaurora.contacts"
    compileSdk = 33
    
    defaultConfig {
        applicationId = "org.codeaurora.contacts"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.preference:preference:1.2.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
}
