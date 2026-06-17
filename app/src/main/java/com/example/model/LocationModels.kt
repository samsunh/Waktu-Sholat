package com.example.model

data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double,
    val description: String
)

val INDONESIAN_CITIES = listOf(
    City("Jakarta", -6.2088, 106.8456, 7.0, "DKI Jakarta (WIB)"),
    City("Bandung", -6.9175, 107.6191, 7.0, "Jawa Barat (WIB)"),
    City("Surabaya", -7.2575, 112.7521, 7.0, "Jawa Timur (WIB)"),
    City("Yogyakarta", -7.7956, 110.3695, 7.0, "DI Yogyakarta (WIB)"),
    City("Medan", 3.5952, 98.6722, 7.0, "Sumatera Utara (WIB)"),
    City("Palembang", -2.9761, 104.7754, 7.0, "Sumatera Selatan (WIB)"),
    City("Makassar", -5.1476, 119.4327, 8.0, "Sulawesi Selatan (WITA)"),
    City("Denpasar", -8.6705, 115.2126, 8.0, "Bali (WITA)"),
    City("Balikpapan", -1.2654, 116.8312, 8.0, "Kalimantan Timur (WITA)"),
    City("Manado", 1.4748, 124.8405, 8.0, "Sulawesi Utara (WITA)"),
    City("Ambon", -3.6554, 128.1906, 9.0, "Maluku (WIT)"),
    City("Jayapura", -2.5916, 140.6690, 9.0, "Papua (WIT)")
)
