plugins {
    id(Plugins.AndoridLib)
    id(Plugins.AndoridKotlinPlugin)
}

android {
    namespace = "com.example.composepicasso"
    compileSdk = ConfigurationData.compileSdk

    defaultConfig {
        minSdk = ConfigurationData.minSdk
        testInstrumentationRunner = ConfigurationData.testInstrumentationRunner
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

    implementation(Libs.AndroidX.Core.ktx)
    /**
     * Compose
     */
    // Fundamental components of compose UI needed to interact with the device, including layout, drawing, and input.
    implementation(Libs.AndroidX.Compose.Ui)
    implementation(Libs.AndroidX.Compose.Foundation)
}