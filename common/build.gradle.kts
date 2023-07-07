plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasm {
//        browser()
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kotlin-library:compose"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
//        val wasmMain by getting
    }
}

// Use a proper version of webpack, TODO remove after updating to Kotlin 1.9.
//rootProject.the<NodeJsRootExtension>().versions.webpack.version = "5.76.2"
//
//compose.experimental {
//    web.application {}
//}
