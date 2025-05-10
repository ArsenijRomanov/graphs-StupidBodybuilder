import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("jacoco") // ✅ Плагин покрытия кода
}

repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // Тестовые библиотеки
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("io.kotest:kotest-property:5.5.5")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")

    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("net.jqwik:jqwik:1.7.2")

    // Для UI-тестов Compose Desktop (если используешь)
    testImplementation(compose.desktop.uiTestJUnit4)

    implementation(compose.foundation)
    implementation(compose.ui)
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinJvmComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}

// Используем JUnit 5
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // Автоматически генерировать отчёт после тестов
}

// JaCoCo настройка
jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Сначала прогнать тесты
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
