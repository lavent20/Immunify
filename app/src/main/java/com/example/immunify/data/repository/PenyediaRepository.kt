package com.example.immunify.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.example.immunify.data.model.DataDummy
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PenyediaRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val client: OkHttpClient,
    @ApplicationContext private val context: Context
) {
    private val penyediaRef = database.reference.child("penyedia_vaksin")
    private val gson = Gson()
    
    // Cache sederhana untuk menyimpan hasil pencarian terakhir agar transisi ke detail cepat & sinkron
    private var lastFetchedClinics: List<PenyediaVaksin> = emptyList()

    private fun getAddressFromCoords(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale("id", "ID"))
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val subLocality = address.subLocality // Kelurahan/Desa
                val locality = address.locality // Kecamatan
                val subAdminArea = address.subAdminArea // Kota/Kabupaten
                val thoroughfare = address.thoroughfare // Nama Jalan
                
                val streetInfo = if (!thoroughfare.isNullOrEmpty()) thoroughfare else "Jl. Sekitar"
                val locationInfo = listOfNotNull(subLocality, locality, subAdminArea).joinToString(", ")
                
                if (locationInfo.isNotEmpty()) "$streetInfo, $locationInfo" else address.getAddressLine(0)
            } else {
                "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            "Alamat terdeteksi di sekitar lokasi Anda"
        }
    }

    fun getClinicByName(name: String): PenyediaVaksin? {
        return lastFetchedClinics.find { it.namaLengkap == name }
    }

    fun getAllPenyedia(): Flow<Resource<List<PenyediaVaksin>>> = callbackFlow {
        trySend(Resource.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(PenyediaVaksin::class.java) }
                trySend(Resource.Success(items))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        penyediaRef.addValueEventListener(listener)
        awaitClose { penyediaRef.removeEventListener(listener) }
    }

    /**
     * MENGAMBIL DATA KLINIK ASLI BERDASARKAN LOKASI (OPENSTREETMAP - GRATIS)
     * Menggunakan strategi 'Fastest Response' + Android Geocoder untuk Alamat Akurat.
     */
    @SuppressLint("MissingPermission")
    fun getRealtimeNearbyClinics(userLocation: android.location.Location): Flow<Resource<List<PenyediaVaksin>>> = callbackFlow {
        trySend(Resource.Loading())

        withContext(Dispatchers.IO) {
            val lat = userLocation.latitude
            val lon = userLocation.longitude
            val radius = 20000 // 20km

            val query = """
                [out:json][timeout:25];
                (
                  node["amenity"~"hospital|clinic|doctors|health_post|pharmacy"](around:$radius,$lat,$lon);
                  way["amenity"~"hospital|clinic|doctors|health_post|pharmacy"](around:$radius,$lat,$lon);
                  relation["amenity"~"hospital|clinic|doctors|health_post|pharmacy"](around:$radius,$lat,$lon);
                );
                out center;
            """.trimIndent()

            val endpoints = listOf(
                "https://overpass-api.de/api/interpreter",
                "https://overpass.kumi.systems/api/interpreter",
                "https://lz4.overpass-api.de/api/interpreter"
            )

            val deferredResult = CompletableDeferred<List<PenyediaVaksin>>()
            val jobs = mutableListOf<Job>()

            endpoints.forEach { baseUrl ->
                val job = launch {
                    try {
                        val url = "$baseUrl?data=${java.net.URLEncoder.encode(query, "UTF-8")}"
                        val request = Request.Builder()
                            .url(url)
                            .header("User-Agent", "ImmunifyApp/1.5")
                            .build()

                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                val body = response.body?.string()
                                val osmResponse = gson.fromJson(body, OsmResponse::class.java)
                                val items = osmResponse.elements.mapNotNull { element ->
                                    val elLat = element.lat ?: element.center?.lat ?: return@mapNotNull null
                                    val elLon = element.lon ?: element.center?.lon ?: return@mapNotNull null
                                    
                                    val distanceInMeters = FloatArray(1)
                                    android.location.Location.distanceBetween(lat, lon, elLat, elLon, distanceInMeters)
                                    
                                    val streetOSM = element.tags?.get("addr:street")
                                    val fullAddrOSM = element.tags?.get("addr:full")
                                    
                                    val finalAddress = if (!fullAddrOSM.isNullOrBlank()) {
                                        fullAddrOSM
                                    } else if (!streetOSM.isNullOrBlank()) {
                                        val hn = element.tags?.get("addr:housenumber")
                                        val city = element.tags?.get("addr:city")
                                        val s = if (!hn.isNullOrBlank()) "$streetOSM No. $hn" else streetOSM
                                        if (!city.isNullOrBlank()) "$s, $city" else s
                                    } else {
                                        // JIKA DI OSM ALAMATNYA TIDAK JELAS, PAKAI GEOCODER ANDROID BIAR MANTAP
                                        getAddressFromCoords(elLat, elLon)
                                    }

                                    PenyediaVaksin(
                                        id = element.id.toString(),
                                        namaLengkap = element.tags?.get("name") ?: "Fasilitas Kesehatan",
                                        jenisVaksin = "Lengkap (Rutin & Tambahan)",
                                        pengalaman = String.format("%.1f km", distanceInMeters[0] / 1000.0),
                                        poin = (44 + (element.id % 6)).toInt(),
                                        imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=400",
                                        alamat = finalAddress,
                                        deskripsi = "Data real-time dari server OpenStreetMap (OSM)."
                                    )
                                }.filter { it.namaLengkap != "Fasilitas Kesehatan" }
                                 .distinctBy { it.namaLengkap }
                                 .sortedBy { it.pengalaman.replace(" km", "").replace(",", ".").toDouble() }

                                if (items.isNotEmpty() && !deferredResult.isCompleted) {
                                    deferredResult.complete(items)
                                }
                            }
                        }
                    } catch (e: Exception) { }
                }
                jobs.add(job)
            }

            try {
                val finalItems = deferredResult.await()
                lastFetchedClinics = finalItems
                trySend(Resource.Success(finalItems))
                coroutineContext.cancelChildren()
            } catch (e: Exception) {
                if (!deferredResult.isCompleted) {
                    trySend(Resource.Error("Kesalahan server atau lokasi tidak ditemukan."))
                }
            }
        }
        awaitClose()
    }

    fun getPenyediaByVaksin(jenisVaksin: String): Flow<Resource<List<PenyediaVaksin>>> = callbackFlow {
        trySend(Resource.Loading())
        val query = penyediaRef.orderByChild("jenisVaksin").equalTo(jenisVaksin)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(PenyediaVaksin::class.java) }
                trySend(Resource.Success(items))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getPenyediaById(id: String): Flow<Resource<PenyediaVaksin>> = callbackFlow {
        trySend(Resource.Loading())
        val ref = penyediaRef.child(id)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val p = snapshot.getValue(PenyediaVaksin::class.java)
                if (p != null) trySend(Resource.Success(p)) else trySend(Resource.Error("Not found"))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}

data class OsmResponse(val elements: List<OsmElement>)
data class OsmElement(
    val id: Long, 
    val lat: Double?, 
    val lon: Double?, 
    val center: OsmCenter?, 
    val tags: Map<String, String>?
)
data class OsmCenter(val lat: Double, val lon: Double)
