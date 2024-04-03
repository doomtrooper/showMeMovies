object Versions {
    const val navVersion = "2.7.7"
    const val composeVersion = "1.6.2"
    const val hiltVersion = "1.2.0"
    const val hiltGoogle = "2.49"
    const val lifeCycle = "1.2.0"
    const val leakCanary = "3.0-alpha-1"
}

object Libs {

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.4.1"

        object Navigation {
            const val FragmentKtx =
                "androidx.navigation:navigation-fragment-ktx:" + Versions.navVersion
            const val UitKtx =
                "androidx.navigation:navigation-fragment-ui-ktx:" + Versions.navVersion
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
        }
    }

    object Google {
        object Hilt {
            const val Android = "com.google.dagger:hilt-android:" + Versions.hiltGoogle
            const val Compiler = "com.google.dagger:hilt-compiler:" + Versions.hiltGoogle
        }
    }
}

object DebugLibs {
    object Square {
        const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:" + Versions.leakCanary
    }
}