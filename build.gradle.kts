plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

tasks.register("coverageReportAll") {
    group = "verification"
    description = "Generate JaCoCo coverage reports for key modules."
    dependsOn(
        ":core:domain:jacocoTestReport",
        ":core:data:jacocoTestReport",
        ":core:database:jacocoTestReport",
        ":feature:todo:impl:jacocoTestReport"
    )
}

tasks.register("coverageVerifyAll") {
    group = "verification"
    description = "Verify JaCoCo coverage thresholds for key modules."
    dependsOn(
        ":core:domain:jacocoCoverageVerification",
        ":core:data:jacocoCoverageVerification",
        ":core:database:jacocoCoverageVerification",
        ":feature:todo:impl:jacocoCoverageVerification"
    )
}
