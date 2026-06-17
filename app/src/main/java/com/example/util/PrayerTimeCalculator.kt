package com.example.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.tan

object PrayerTimeCalculator {
    private const val SUBUH_ANGLE = 20.0
    private const val ISYA_ANGLE = 18.0

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        timezone: Double, // e.g., 7.0 for WIB
        year: Int,
        month: Int, // 1-12
        day: Int
    ): Map<String, String> {
        fun doubleToTime(time: Double): String {
            if (time.isNaN()) return "--:--"
            var t = time
            while (t < 0) t += 24.0
            while (t >= 24) t -= 24.0
            val hours = floor(t).toInt()
            val minutes = round((t - hours) * 60).toInt()
            val finalHours = if (minutes == 60) (hours + 1) % 24 else hours
            val finalMinutes = if (minutes == 60) 0 else minutes
            return String.format(Locale.US, "%02d:%02d", finalHours, finalMinutes)
        }

        // Julian Date
        val m = month
        val y = if (m <= 2) year - 1 else year
        val mm = if (m <= 2) m + 12 else m

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (mm + 1)) + day + b - 1524.5

        // Days since J2000
        val d = jd - 2451545.0

        // Mean anomalies of sun
        val g = 357.529 + 0.98560028 * d
        val q = 280.459 + 0.9856474 * d
        var l = q + 1.915 * sin(Math.toRadians(g)) + 0.02 * sin(Math.toRadians(2 * g))
        while (l < 0) l += 360.0
        while (l >= 360) l -= 360.0

        // Obliquity of ecliptic
        val e = 23.439 - 0.00000036 * d

        // Right ascension / Sun's declination
        var ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))
        while (ra < 0) ra += 360.0
        while (ra >= 360) ra -= 360.0
        val dec = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))

        // Equation of time (in hours)
        val eqt = q / 15.0 - ra / 15.0
        // Noon transit (dhuhr base)
        val baseMidday = 12.0 - longitude / 15.0 + timezone - eqt

        // Midday / Dzuhur (add 2 minutes safety buffer)
        val dhuhr = baseMidday + (2.0 / 60.0)

        // Angle helper for prayer times (hour angle)
        fun hourAngle(angle: Double, dec: Double, lat: Double, isSunrise: Boolean = false): Double {
            val h = if (isSunrise) angle else angle
            val num = -sin(Math.toRadians(h)) - sin(Math.toRadians(lat)) * sin(Math.toRadians(dec))
            val den = cos(Math.toRadians(lat)) * cos(Math.toRadians(dec))
            val cosAlpha = num / den
            if (cosAlpha > 1.0 || cosAlpha < -1.0) return Double.NaN
            return Math.toDegrees(acos(cosAlpha)) / 15.0
        }

        // Subuh (Dawn)
        val haSubuh = hourAngle(SUBUH_ANGLE, dec, latitude)
        val subuh = dhuhr - haSubuh

        // Syuruk (Sunrise) at 0.833 degrees
        val haSunrise = hourAngle(0.833, dec, latitude, isSunrise = true)
        val sunrise = dhuhr - haSunrise

        // Ashar (Syafi'i method: shadow = shadow_0 + 1)
        val shadowLength = 1.0
        val acotTerm = Math.toDegrees(atan(1.0 / (shadowLength + tan(Math.toRadians(abs(latitude - dec))))))
        val haAsr = hourAngle(90.0 - acotTerm, dec, latitude)
        val asr = dhuhr + haAsr

        // Maghrib (Sunset) at 0.833 degrees + safety offset (2 minutes)
        val maghrib = dhuhr + haSunrise + (2.0 / 60.0)

        // Isya
        val haIsha = hourAngle(ISYA_ANGLE, dec, latitude)
        val isha = dhuhr + haIsha

        return mapOf(
            "Subuh" to doubleToTime(subuh),
            "Syuruk" to doubleToTime(sunrise),
            "Dzuhur" to doubleToTime(dhuhr),
            "Ashar" to doubleToTime(asr),
            "Maghrib" to doubleToTime(maghrib),
            "Isya" to doubleToTime(isha)
        )
    }
}
