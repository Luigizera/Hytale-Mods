plugins {
    id("java")
}

group = "com.ludas"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
}

tasks.jar{
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/resources")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("deployToServer") {
    dependsOn(tasks.jar)
    from(tasks.jar.get().archiveFile)
    into(file("../../Desktop/projetoHytale/hytaleserver/mods"))
}