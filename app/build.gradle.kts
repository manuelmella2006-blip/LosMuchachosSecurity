plugins {
    alias(libs.plugins.android.application)      // Plugin principal de Android
    id("com.google.gms.google-services")         // 🔥 Integración con Firebase
}

android {
    namespace = "com.example.losmuchachossecurity"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.losmuchachossecurity"
        minSdk = 34
        targetSdk = 36
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
    // 🔹 Dependencias base de Android
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.android.material:material:1.9.0")

    // 🔥 Firebase (usando Firebase BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Módulos de Firebase que usas
    implementation("com.google.firebase:firebase-auth")       // Autenticación
    implementation("com.google.firebase:firebase-firestore")  // Base de datos Firestore
    implementation("com.google.firebase:firebase-analytics")  // (Opcional) analíticas

    // 🎨 Material Design
    implementation("com.google.android.material:material:1.11.0")

    // 📄 Librería para generar PDFs
    implementation("com.itextpdf:itextg:5.5.10")

    // 🔹 Librerías de pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
