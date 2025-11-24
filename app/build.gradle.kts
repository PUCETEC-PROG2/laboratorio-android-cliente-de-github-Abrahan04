import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ec.edu.uisek.githubclient"
    compileSdk = 36

    defaultConfig {
        applicationId = "ec.edu.uisek.githubclient"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- SOLUCIÓN DEL ERROR DEL TOKEN ---
        // Configurar la variable de entorno desde local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        val githubToken = localProperties.getProperty("GITHUB_API_TOKEN") ?: ""

        // Inyectar el token en BuildConfig
        buildConfigField("String", "GITHUB_API_TOKEN", "\"$githubToken\"")
        // -------------------------------------
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit para networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Convertidor Gson para serializar/deserializar JSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Interceptor de logs para depurar las llamadas de red
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Glide para cargar imágenes desde URLs
    implementation("com.github.bumptech.glide:glide:4.16.0")
}