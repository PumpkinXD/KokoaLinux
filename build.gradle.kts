import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.OperatingSystemInternal
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "cn.yesterday17.kokoalinux"
version = "1.2.2-alpha.0.1.1"

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}
val tweaker = "cn.yesterday17.kokoalinux.tweaker.KokoaFMLTweaker"

// Minecraft configuration:
loom {
    log4jConfigs.from(file("log4j2.xml"))
    launchConfigs {
        "client" {
            // If you don't want mixins, remove these lines
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            property("fml.coreMods.load", tweaker)
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            arg("--mixin", "mixins.kokoalinux.json")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        // If you don't want mixins, remove this lines
        mixinConfig("mixins.kokoalinux.json")
    }
    // If you don't want mixins, remove these lines
    mixin {
        defaultRefmapName.set("mixins.kokoalinux.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

// Dependencies:

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    // If you don't want to log in with your real minecraft account, remove this line
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    // If you don't want mixins, remove these lines
    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    annotationProcessor("org.spongepowered:mixin:0.8.4-SNAPSHOT")

    // If you don't want to log in with your real minecraft account, remove this line
    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.0")

}

// Tasks:

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set("KokoaLinux")
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"
        this["FMLCorePlugin"] = tweaker

        // If you don't want mixins, remove these lines
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixins.kokoalinux.json"
    }
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}


tasks.shadowJar {

    from("./libkokoa/build/"
            +DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName
            +"-"
            +DefaultNativePlatform.getCurrentArchitecture().name)
    {
        include(System.mapLibraryName("kokoa"))
        if(!DefaultNativePlatform.getCurrentOperatingSystem().isSolaris)
            into(DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName
                    +"-"
                    +DefaultNativePlatform.getCurrentArchitecture().name)
        else into("sunos"
                +"-"
                +DefaultNativePlatform.getCurrentArchitecture().name)
    }



    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying jars into mod: ${it.files}")
        }
    }

    // If you want to include other dependencies and shadow them, you can relocate them in here
    fun relocate(name: String) = relocate(name, "cn.yesterday17.kokoalinux.deps.$name")
}

tasks.assemble.get().dependsOn(tasks.remapJar)

tasks.processResources{
    filesMatching("mcmod.info"){
        expand(
            "version" to project.version, "mcversion" to "1.8.9"
        )
    }
}

val buildLibkokoa by tasks.registering {

    fun String.execute() = org.codehaus.groovy.runtime.ProcessGroovyMethods.execute(this)
    fun Process.waitForProcessOutput(out: java.io.OutputStream,err: java.io.OutputStream)= org.codehaus.groovy.runtime.ProcessGroovyMethods.waitForProcessOutput(this,out,err)

println(DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName+"-"+DefaultNativePlatform.getCurrentArchitecture().name)
    if (DefaultNativePlatform.getCurrentOperatingSystem().isLinux()||DefaultNativePlatform.getCurrentOperatingSystem().isFreeBSD()||DefaultNativePlatform.getCurrentOperatingSystem().isSolaris()) {
        val buildcmd = arrayOf("libkokoa/build.sh",DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName+"-"+DefaultNativePlatform.getCurrentArchitecture().name)
        val cleancmd = arrayOf("libkokoa/clean.sh",DefaultNativePlatform.getCurrentOperatingSystem().internalOs.familyName+"-"+DefaultNativePlatform.getCurrentArchitecture().name)

        //org.codehaus.groovy.runtime.ProcessGroovyMethods.execute(buildcmd).waitForProcessOutput(System.out,System.err)
        //org.codehaus.groovy.runtime.ProcessGroovyMethods.execute(cleancmd).waitForProcessOutput(System.out,System.err)

        Runtime.getRuntime().exec(buildcmd).waitForProcessOutput(System.out,System.err)
        Runtime.getRuntime().exec(cleancmd).waitForProcessOutput(System.out,System.err)
    }
    //TODO:download libkokoa natives from github action/release for other archs(and oses)
}