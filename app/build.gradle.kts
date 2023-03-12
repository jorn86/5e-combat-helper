import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.compose") version "1.3.0"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    api(project(":core"))
    api(project(":compose-library"))

    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)

//    implementation("com.halilibo.compose-richtext:richtext-ui-material:0.16.0")

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")

    implementation("org.slf4j:slf4j-simple:2.0.6")
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
