package com.newaura.bookish.core.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.newaura.bookish.core.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object WebViewUtil {

    // ✅ Detect environment and return correct URL
    fun getCorrectUrl(port: Int = 3000): String {
        return try {
            // Check if running on emulator
            val isEmulator = (android.os.Build.FINGERPRINT.contains("generic")
                    || android.os.Build.MODEL.contains("Emulator")
                    || android.os.Build.MODEL.contains("Android SDK"))

            if (isEmulator) {
                "http://10.0.2.2:$port"  // ✅ Emulator localhost
            } else {
                "http://192.168.1.100:$port"  // ✅ Replace with your PC IP
            }
        } catch (e: Exception) {
            "http://10.0.2.2:$port"
        }
    }

    // ✅ Check if server is reachable
    suspend fun isServerReachable(host: String, port: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), 3000)
                    true
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    // ✅ Check network availability
    fun isNetworkAvailable(context: ApplicationContext): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}