import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    // Suppress expect/actual beta warning
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        // Configure web source set hierarchy
        val webMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(npm("@js-joda/timezone", "2.22.0"))
            }
        }
        wasmJsMain.get().dependsOn(webMain)

        // Configure iOS source set hierarchy
        val iosMain by creating {
            dependsOn(commonMain.get())
        }
        iosArm64Main.get().dependsOn(iosMain)
        iosSimulatorArm64Main.get().dependsOn(iosMain)

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // New
            implementation(libs.ktor.client.android)
            implementation(libs.android.driver)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // ML Kit Dependencies
            implementation("com.google.mlkit:text-recognition:16.0.1")

            // CameraX
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.extensions)

            implementation(libs.accompanist.permissions)
            implementation(libs.concurrent.futures)
            implementation(libs.androidx.camera.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
            implementation(compose.materialIconsExtended)

            // New
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.runtime)
            implementation(libs.coroutines.extensions)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            // New
            implementation(libs.sqlite.driver)
        }
        iosMain.dependencies {
            // New
            implementation(libs.ktor.client.darwin)
            implementation(libs.native.driver)
        }
    }
}

android {
    namespace = "com.bagi_bill.bagi_bill"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.bagi_bill.bagi_bill"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.bagi_bill.bagi_bill.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.bagi_bill.bagi_bill"
            packageVersion = "1.0.0"
        }
        windows {
            menu = true
            shortcut = true
            // icon = file("src/desktopMain/resources/icon.ico") // Jika punya icon
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.bagi_bill.bagi_bill.database")
        }
    }
}
