import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasm {
        moduleName = "initiative"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    open = mapOf(
                        "app" to mapOf(
                            "name" to "google-chrome",
                            "arguments" to listOf("--js-flags=--experimental-wasm-gc ")
                        )
                    ),
                    static = (devServer?.static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        val sub = "build/processedResources/wasm/main"
                        add(project.rootDir.path + sub)
                        add(project.rootDir.path + "/common/$sub")
                        add(project.rootDir.path + "/kotlin-library/core/$sub")
                        add(project.rootDir.path + "/kotlin-library/compose/$sub")
                        add(project.rootDir.path + "/web/$sub")
                    },
                )
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val wasmMain by getting {
            dependencies {
                api(project(":common"))
            }
        }
        val wasmTest by getting
    }
}

// Use a proper version of webpack, TODO remove after updating to Kotlin 1.9.
rootProject.the<NodeJsRootExtension>().versions.webpack.version = "5.76.2"

compose.experimental {
    web.application {}
}
