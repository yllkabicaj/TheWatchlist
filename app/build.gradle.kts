plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.thewatchlist"
    compileSdk = 34

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/DEPENDENCIES.txt")
        exclude("META-INF/gfprobe-provider.xml")
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
    }

    defaultConfig {
        applicationId = "com.example.thewatchlist"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.sendgrid:sendgrid-java:4.7.0")
    implementation("com.sun.mail:android-mail:1.6.2")
    implementation("com.sun.mail:android-activation:1.6.2")
}