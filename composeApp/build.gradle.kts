import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.3.0"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                
                // Serialization for JSON storage
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Pkg)
            packageName = "WorkTimeTracker"
            packageVersion = "1.0.0"
            description = "퇴근시간 & 월급날 추적기"
            copyright = "© 2025"
            vendor = "Fru"
            
            macOS {
                bundleID = "com.worktime.tracker"
                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
                minimumSystemVersion = "11.0"
            }
        }
    }
}


