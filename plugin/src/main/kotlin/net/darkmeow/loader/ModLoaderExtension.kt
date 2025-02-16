package net.darkmeow.loader

import org.gradle.api.provider.Property

abstract class ModLoaderExtension {
    abstract val directClass: Property<String>
    abstract val forgeModClass: Property<String>

    abstract val verifyClass: Property<String>

    abstract val disableSplash: Property<Boolean>
}