plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = ConfigurationData.namespace
    compileSdk = ConfigurationData.compileSdk

    defaultConfig {
        applicationId = ConfigurationData.applicationId
        minSdk = ConfigurationData.minSdk
        targetSdk = ConfigurationData.targetSdk
        versionCode = ConfigurationData.versionCode
        versionName = ConfigurationData.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
}

dependencies {
    implementation(project(":movies"))
    implementation(project(":data"))
    /**
     * Navigation
     */
    implementation(Libs.AndroidX.Navigation.FragmentKtx)
    implementation(Libs.AndroidX.Navigation.UitKtx)
    implementation(Libs.AndroidX.Navigation.Compose)


    /**
     * Compose
     */
    // Fundamental components of compose UI needed to interact with the device, including layout, drawing, and input.
    implementation(Libs.AndroidX.Compose.Ui)
    // Build Jetpack Compose UIs with ready to use Material Design Components.
    // This is the higher level entry point of Compose, designed to provide components that match those described at www.material.io.
    implementation(Libs.AndroidX.Compose.Material)
    // Write Jetpack Compose applications with ready to use building blocks
    // and extend foundation to build your own design system pieces.
    implementation(Libs.AndroidX.Compose.Foundation)
    implementation(Libs.AndroidX.Compose.ToolingPreview)

    implementation(Libs.AndroidX.Hilt.Navigation)
    // https://developer.android.com/jetpack/compose/state
    implementation(Libs.AndroidX.LifeCycle.Compose)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //hilt
    implementation(Libs.Google.Hilt.Android)
    ksp(Libs.Google.Hilt.Compiler)

    debugImplementation(DebugLibs.Square.LeakCanary)
}

