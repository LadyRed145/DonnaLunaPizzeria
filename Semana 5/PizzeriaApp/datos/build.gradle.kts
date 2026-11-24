plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.donnaluna.pizzeriaapp.datos"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":utilidades"))
    testImplementation(libs.junit)
}
