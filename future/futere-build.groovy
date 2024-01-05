import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.1'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'ru.clevertec.banking'
version = '0.0.1'

java {
    sourceCompatibility = '17'
}

springBoot {
    mainClass = 'ru.clevertec.banking.ClevertecFinalProjectApplication'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-amqp'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.liquibase:liquibase-core'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    runtimeOnly 'org.postgresql:postgresql'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.amqp:spring-rabbit-test'
    testImplementation 'org.springframework.security:spring-security-test'
}

def activeProfiles=project.properties['activeProfiles'] ?: "dev"

processResources {
    filter ReplaceTokens, tokens: [
            activeProfiles: activeProfiles
    ]
}

test {
    useJUnitPlatform()
}
