package eryaz.software.activegroup.data.api.utils

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import eryaz.software.activegorup.data.BuildConfig

object NetworkUtils {

    private var startOfLocalHostIp = "192.168.22.26"

    private fun getWifiIpAddress(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo

        val ipAddress = wifiInfo?.ipAddress ?: 0

        return (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)

    }

    fun getIpAddressTypeOutOrIn(context: Context): String {

        val ipAddress = getWifiIpAddress(context)
        println(ipAddress)
        return if (ipAddress.contains(startOfLocalHostIp)) {
            BuildConfig.BASE_URL
        } else {
            BuildConfig.BASE_OUT_URL
        }
    }

}