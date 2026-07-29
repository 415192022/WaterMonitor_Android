package com.lmw.watermonitorandroid.domain.system.impl

import android.content.SharedPreferences
import com.lmw.watermonitorandroid.domain.system.api.ServerAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerAddressStore @Inject constructor(
    private val prefs: SharedPreferences
) {
    companion object {
        private const val KEY_IP = "server_ip"
        private const val KEY_PORT = "server_port"
        private const val DEFAULT_IP = "192.168.4.1"
        private const val DEFAULT_PORT = 80
    }

    fun getAddress(): ServerAddress {
        return ServerAddress(
            ip = prefs.getString(KEY_IP, DEFAULT_IP) ?: DEFAULT_IP,
            port = prefs.getInt(KEY_PORT, DEFAULT_PORT)
        )
    }

    fun saveAddress(address: ServerAddress): Boolean {
        return prefs.edit()
            .putString(KEY_IP, address.ip)
            .putInt(KEY_PORT, address.port)
            .commit()
    }
}