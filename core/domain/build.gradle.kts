plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(project(":core:testing"))
}

kotlin {
    jvmToolchain(17)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.named<org.gradle.testing.jacoco.tasks.JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/*$*.*"
    )

    classDirectories.setFrom(
        fileTree("$buildDir/classes/kotlin/main") { exclude(fileFilter) },
        fileTree("$buildDir/classes/java/main") { exclude(fileFilter) }
    )
    sourceDirectories.setFrom(files("src/main/kotlin", "src/main/java"))
    executionData.setFrom(fileTree(buildDir) { include("jacoco/test.exec") })
}

tasks.register<org.gradle.testing.jacoco.tasks.JacocoCoverageVerification>("jacocoCoverageVerification") {
    dependsOn(tasks.test)

    val fileFilter = listOf(
        "**/*$*.*"
    )

    classDirectories.setFrom(
        fileTree("$buildDir/classes/kotlin/main") { exclude(fileFilter) },
        fileTree("$buildDir/classes/java/main") { exclude(fileFilter) }
    )
    sourceDirectories.setFrom(files("src/main/kotlin", "src/main/java"))
    executionData.setFrom(fileTree(buildDir) { include("jacoco/test.exec") })

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.85".toBigDecimal()
            }
        }
    }
}
