// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
    }
}

plugins {
    id (Plugins.AndoridGradlePlugin) version Plugins.AndoridGradlePluginVersion apply false
    id (Plugins.AndoridLib) version Plugins.AndoridLibVersion apply false
    id (Plugins.AndoridKotlinPlugin) version Plugins.AndoridKotlinPluginVersion apply false
    id (Plugins.AndoridHiltPlugin) version Plugins.AndoridHiltPluginVersion apply false
    id (Plugins.AndoridKspPlugin) version Plugins.AndoridKspPluginVersion apply false
}
