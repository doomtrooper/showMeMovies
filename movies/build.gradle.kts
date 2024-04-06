plugins {
    id(Plugins.AndoridLib)
    id(Plugins.AndoridKotlinPlugin)
    id(Plugins.AndoridKspPlugin)
    id(Plugins.AndoridHiltPlugin)
}

android {
    namespace = ConfigurationData.namespace
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = ConfigurationData.testInstrumentationRunner
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        kotlinCompilerExtensionVersion = Versions.kotlinCompilerExtensionVersion
    }
}

dependencies {
    implementation(project(":data"))

    implementation(Libs.AndroidX.Core.ktx)
    implementation(Libs.AndroidX.appcompat)

    testImplementation(TestLibs.jUnit)
    androidTestImplementation(TestLibs.andoridXTestJunit)
    testImplementation(TestLibs.mockk)
    testImplementation(TestLibs.kotlinCoroutinesTest)

    /**
     * Compose
     */
    // Fundamental components of compose UI needed to interact with the device, including layout, drawing, and input.
    implementation(Libs.AndroidX.Compose.Ui)
    // Build Jetpack Compose UIs with ready to use Material Design Components.
    // This is the higher level entry point of Compose, designed to provide components that match those described at www.material.io.
    implementation(Libs.AndroidX.Compose.Material)
    implementation(Libs.AndroidX.Compose.ToolingPreview)
    // Fundamental components of compose UI needed to interact with the device, including layout, drawing, and input.
    // Build Jetpack Compose UIs with ready to use Material Design Components.
    // This is the higher level entry point of Compose, designed to provide components that match those described at www.material.io.
    // Write Jetpack Compose applications with ready to use building blocks
    // and extend foundation to build your own design system pieces.
    implementation(Libs.AndroidX.Compose.Foundation)

    implementation(Libs.coil)
    // ViewModel
    implementation(Libs.AndroidX.LifeCycle.viewmodelKtx)

    //hilt
    implementation(Libs.Google.Hilt.Android)
    ksp(Libs.Google.Hilt.Compiler)

}

