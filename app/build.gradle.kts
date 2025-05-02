plugins {

    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "hu.matusz.travelapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "hu.matusz.travelapp"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.credentials:credentials:1.2.0-alpha03")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.0.0-beta02")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}