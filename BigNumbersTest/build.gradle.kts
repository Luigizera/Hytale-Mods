plugins {
    id("java")
}

group = "Github.Luigizera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "hytale"
        url = uri("https://maven.hytale.com/release") // Or "hytale-pre-release" for pre-release versions
    }
}

dependencies {
    //compileOnly(files("libs/HytaleServer.jar"))
    implementation("com.hypixel.hytale:Server:+")
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