import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs += "-Xjvm-default=all" // allow MagicMap to handle default methods
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            systemProperties["norrFolder"] = System.getProperty("norrFolder")
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                api(project(":kotlin-library:compose"))

                implementation(compose.desktop.currentOs)
                implementation(compose.materialIconsExtended)

                implementation("com.fasterxml.jackson.core:jackson-core:2.14.1")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
                implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
                implementation("com.google.guava:guava:32.1.1-jre")

                implementation("org.slf4j:slf4j-simple:2.0.6")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "org.hertsig.dnd.combat.AppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "dnd.combat"
            packageVersion = "1.0.0"
        }
    }
}
