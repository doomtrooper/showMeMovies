object Versions {
    const val navVersion = "2.7.7"
    const val composeVersion = "1.6.2"
    const val coreVersion = "1.12.0"
    const val appCompat = "1.6.1"
    const val hiltVersion = "1.2.0"
    const val hiltGoogle = "2.49"
    const val lifeCycle = "1.2.0"
    const val leakCanary = "3.0-alpha-1"
    const val room = "2.6.1"
    const val retrofit = "2.9.0"
    const val viewModel = "2.7.0"
}

object Libs {
    const val coil = "io.coil-kt:coil-compose:2.6.0"
    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:"+Versions.appCompat

        object Navigation {
            const val FragmentKtx =
                "androidx.navigation:navigation-fragment-ktx:" + Versions.navVersion
            const val UitKtx =
                "androidx.navigation:navigation-ui-ktx:" + Versions.navVersion
            const val Compose = "androidx.navigation:navigation-compose:" + Versions.navVersion
        }

        object Compose {
            const val Ui = "androidx.compose.ui:ui:${Versions.composeVersion}"
            const val Material = "androidx.compose.material:material:" + Versions.composeVersion
            const val Foundation =
                "androidx.compose.foundation:foundation:" + Versions.composeVersion
            const val ToolingPreview =
                "androidx.compose.ui:ui-tooling-preview-android:" + Versions.composeVersion
        }

        object Hilt {
            const val Navigation = "androidx.hilt:hilt-navigation-compose:" + Versions.hiltVersion
        }

        object LifeCycle {
            const val Compose = "androidx.lifecycle:lifecycle-runtime-compose:" + Versions.lifeCycle
            const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:" + Versions.viewModel
        }

        object Core{
            const val ktx = "androidx.core:core-ktx:" +Versions.coreVersion
        }

        object Room{
            const val room = "androidx.room:room-runtime:"+Versions.room
            const val roomKtx = "androidx.room:room-ktx:"+Versions.room
            const val roomCompiler = "androidx.room:room-compiler:"+Versions.room
            const val roomTesting = "androidx.room:room-testing:"+Versions.room
        }
    }

    object Google {
        object Hilt {
            const val Android = "com.google.dagger:hilt-android:" + Versions.hiltGoogle
            const val Compiler = "com.google.dagger:hilt-compiler:" + Versions.hiltGoogle
        }
    }

    object Square {
        object Retrofit{
            const val retrofit = "com.squareup.retrofit2:retrofit:"+Versions.retrofit
            const val retrofitGson = "com.squareup.retrofit2:converter-gson:"+Versions.retrofit
        }
    }

    object OkHttp{
        const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:4.11.0"
    }
}

object DebugLibs {
    object Square {
        const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:" + Versions.leakCanary
    }
}

object TestLibs{
    const val jUnit = "junit:junit:4.13.2"
    const val andoridXTestJunit = "androidx.test.ext:junit:1.1.5"
    const val mockk = "io.mockk:mockk:1.13.10"
    const val kotlinCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0"
    const val turbine = "app.cash.turbine:turbine:1.1.0"

}