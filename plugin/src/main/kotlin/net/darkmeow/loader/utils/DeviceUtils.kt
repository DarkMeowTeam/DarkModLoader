package net.darkmeow.loader.utils

import oshi.SystemInfo
import java.security.MessageDigest

object DeviceUtils {
    fun getDeviceHardwareId(): String {
        val system = SystemInfo()

        return StringBuilder()
            // CPU 信息
            .append(system.hardware.processor.processorIdentifier.name)
            .append(system.hardware.processor.processorIdentifier.processorID)
            // BIOS 信息
            .append(system.hardware.computerSystem.hardwareUUID)
            .toString()
            .let { t ->
                MessageDigest.getInstance("MD5").digest(t.toByteArray()).joinToString("") { "%02x".format(it) }
            }
    }
}