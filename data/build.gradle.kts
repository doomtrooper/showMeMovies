plugins {
    id(Plugins.AndoridLib)
    id(Plugins.AndoridKotlinPlugin)
    id(Plugins.AndoridKspPlugin)
    id(Plugins.AndoridHiltPlugin)
}

android {
    namespace = ConfigurationData.namespace
    compileSdk = ConfigurationData.compileSdk

    defaultConfig {
        minSdk = ConfigurationData.minSdk
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
}

dependencies {

    implementation(Libs.AndroidX.Room.room)
    implementation(Libs.AndroidX.Room.roomKtx)
    // To use Kotlin Symbol Processing (KSP)
    ksp(Libs.AndroidX.Room.roomCompiler)
    testImplementation(Libs.AndroidX.Room.roomTesting)

    // retrofit
    implementation(Libs.Square.Retrofit.retrofit)
    implementation(Libs.Square.Retrofit.retrofitGson)
    implementation(Libs.OkHttp.okHttpLogging)

    //hilt
    implementation(Libs.Google.Hilt.Android)
    ksp(Libs.Google.Hilt.Compiler)

    implementation(Libs.AndroidX.Core.ktx)

    testImplementation(TestLibs.jUnit)
    androidTestImplementation(TestLibs.andoridXTestJunit)

    // Optional -- Mockito framework
    val mockkVersion = "1.13.10"
    testImplementation(TestLibs.mockk)
    testImplementation(TestLibs.kotlinCoroutinesTest)
    testImplementation(TestLibs.turbine)
}
