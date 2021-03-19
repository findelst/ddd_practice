dependencies {
    // project
    implementation(project(":common:types"))
    implementation(project(":order:domain"))
    implementation(project(":order:usecase"))
    
    // kotlin
    implementation(Libs.kotlin_jdk8)
    implementation(Libs.kotlin_reflect)
    implementation(Libs.kotlin_stdlib)
    implementation(Libs.arrow)
    
    // spring-boot
    implementation(Libs.spring_boot_starter_web)

    // test
    testImplementation(Libs.kotest_junit)
    testImplementation(Libs.kotest_arrow)
    testImplementation(Libs.junit_engine)
    testImplementation(Libs.junit_params)
    testImplementation(Libs.spring_boot_starter_test)
}