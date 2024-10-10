plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.exe201"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.exe201"
        minSdk = 24
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }

}

dependencies {

    implementation(libs.appcompat) {
        exclude(group = "com.google.guava")
    }
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.firebase.database)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.volley) // để call api
    implementation(libs.imagepicker)
    implementation(libs.glide) // Thêm Glide để load ảnh
    annotationProcessor(libs.glide.compiler) // Thêm Glide Compiler để load ảnh
    implementation(libs.circleimageview)
    implementation(libs.gson)
    implementation(libs.ucrop)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.viewpager2)
    implementation(libs.mpAndroidChart)

    implementation(libs.google.api.services.sheets) {
        exclude(group = "com.google.guava")
    }

    implementation(libs.google.auth.library.oauth2.http)

    implementation(libs.android.maps.utils)

}