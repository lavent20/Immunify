package com.example.immunify.data.model

object DataDummy {
    val jadwalVaksinList = listOf(
        VaksinJadwal("1", "HPV", "Preventif", "Dosis ke-2", 3, UrgencyLevel.URGENT),
        VaksinJadwal("2", "Influenza", "Tahunan", "Dosis ke-1", 5, UrgencyLevel.SOON),
        VaksinJadwal("3", "Varicella", "Preventif", "Dosis ke-1", 180, UrgencyLevel.LATER)
    )

    val artikelList = listOf(
        Artikel(
            id = "1",
            judul = "Vaksin RSV Pertama Berpotensi Disetujui Tahun Ini",
            tag = "RSV",
            durasiMenit = 5,
            waktuLabel = "24 Januari 2026",
            emoji = "🧬",
            konten = "Para ilmuwan optimis vaksin RSV disetujui tahun ini untuk melindungi bayi dan lansia dari infeksi pernapasan akut."
        ),
        Artikel(
            id = "2",
            judul = "Mengejar Ketertinggalan Imunisasi Rutin Polio dan Campak Anak",
            tag = "Imunisasi",
            durasiMenit = 7,
            waktuLabel = "26 Januari 2026",
            emoji = "👶",
            konten = "Kemenkes meluncurkan program kejar vaksinasi gratis, termasuk imunisasi polio suntik (IPV) dan campak untuk balita."
        ),
        Artikel(
            id = "3",
            judul = "Efek Samping Vaksin Polio: Mana yang Normal?",
            tag = "Mitos & Fakta",
            durasiMenit = 4,
            waktuLabel = "Seminggu lalu",
            emoji = "🌡️",
            konten = "Demam ringan pasca imunisasi polio tetes atau suntik adalah hal wajar yang menandakan sistem imun sedang bekerja."
        ),
        Artikel(
            id = "4",
            judul = "Pentingnya Menjaga Pola Makan Sehat Pasca Imunisasi",
            tag = "Hidup Sehat",
            durasiMenit = 6,
            waktuLabel = "2 hari lalu",
            emoji = "🥗",
            konten = "Nutrisi seimbang membantu mempercepat pembentukan antibodi tubuh secara optimal setelah menerima vaksinasi."
        ),
        Artikel(
            id = "5",
            judul = "Mengapa Deteksi Dini Gejala Polio Sangat Penting bagi Bayi",
            tag = "Polio",
            durasiMenit = 5,
            waktuLabel = "3 hari lalu",
            emoji = "🚼",
            konten = "Gejala awal polio menyerupai flu biasa, tetapi dapat berkembang cepat menjadi kelumpuhan permanen jika tidak diantisipasi."
        ),
        Artikel(
            id = "6",
            judul = "Panduan Olahraga Rutin untuk Menjaga Imunitas Tubuh",
            tag = "Hidup Sehat",
            durasiMenit = 4,
            waktuLabel = "4 hari lalu",
            emoji = "🏃",
            konten = "Olahraga ringan 30 mnt sehari terbukti meningkatkan sirkulasi sel imun melawan infeksi virus berbahaya."
        ),
        Artikel(
            id = "7",
            judul = "Mengenal Kanker Serviks dan Vaksin HPV Sebagai Pencegah",
            tag = "HPV",
            durasiMenit = 8,
            waktuLabel = "5 hari lalu",
            emoji = "🎀",
            konten = "Kanker serviks dapat dicegah secara efektif melalui pemberian vaksin HPV sejak usia remaja sekolah dasar."
        ),
        Artikel(
            id = "8",
            judul = "Jadwal Lengkap Imunisasi DPT untuk Anak Sekolah",
            tag = "Imunisasi",
            durasiMenit = 5,
            waktuLabel = "6 hari lalu",
            emoji = "🏫",
            konten = "Vaksin DPT memberikan perlindungan jangka panjang terhadap penyakit Difteri, Pertusis, dan Tetanus."
        ),
        Artikel(
            id = "9",
            judul = "Mitos Seputar Vaksinasi Dewasa yang Perlu Anda Tahu",
            tag = "Mitos & Fakta",
            durasiMenit = 6,
            waktuLabel = "1 minggu lalu",
            emoji = "🔍",
            konten = "Anggapan bahwa vaksin hanya untuk anak kecil adalah keliru; orang dewasa tetap memerlukan booster seperti Influenza."
        ),
        Artikel(
            id = "10",
            judul = "Manfaat Hidrasi Cukup Sebelum dan Sesudah Divaksin",
            tag = "Hidup Sehat",
            durasiMenit = 3,
            waktuLabel = "2 minggu lalu",
            emoji = "💧",
            konten = "Minum air putih yang cukup meminimalisir efek pusing ringan atau pegal pada lengan pasca penyuntikan vaksin."
        ),
        Artikel(
            id = "11",
            judul = "Kisah Keberhasilan Indonesia Mencegah Penyakit Polio",
            tag = "Imunisasi",
            durasiMenit = 6,
            waktuLabel = "3 minggu lalu",
            emoji = "🇮🇩",
            konten = "Melalui Pekan Imunisasi Nasional, Indonesia berhasil mempertahankan status bebas polio, namun kewaspadaan tetap dijaga."
        )
    )

    val diseaseInsightsList = listOf(
        DiseaseInsight(
            id = "hpv",
            name = "HPV (Human Papillomavirus)",
            emoji = "🧬",
            imageBgColor = 0xFF880E4F,
            keyFacts = listOf(
                "Menular melalui kontak kulit langsung saat aktivitas seksual.",
                "Penyebab utama kasus kanker serviks pada wanita.",
                "Sangat dianjurkan untuk diberikan kepada remaja perempuan dan laki-laki."
            ),
            overview = "HPV adalah virus yang umum menyerang sistem reproduksi dan dapat memicu lesi hingga kanker jika tidak dicegah."
        ),
        DiseaseInsight(
            id = "polio",
            name = "Polio (Poliomyelitis)",
            emoji = "🦠",
            imageBgColor = 0xFF004D40,
            keyFacts = listOf(
                "Menyerang sistem saraf pusat dan menyebabkan kelumpuhan layu akut.",
                "Sangat menular melalui makanan atau air yang tercemar feses virus polio.",
                "Belum ada obatnya, hanya bisa dicegah total dengan vaksin polio (tetes/suntik)."
            ),
            overview = "Polio adalah penyakit menular berbahaya yang menyerang balita, berpotensi memicu cacat permanen seumur hidup."
        ),
        DiseaseInsight(
            id = "covid",
            name = "COVID-19 (Coronavirus)",
            emoji = "😷",
            imageBgColor = 0xFF1565C0,
            keyFacts = listOf(
                "Menyerang saluran pernapasan atas hingga paru-paru.",
                "Menular lewat droplet udara saat batuk, bersin, atau berbicara.",
                "Proteksi diperkuat dengan vaksin utama dan dosis booster berkala."
            ),
            overview = "Infeksi virus corona yang memicu gangguan pernapasan akut, varian mutatifnya membutuhkan adaptasi kekebalan konstan."
        ),
        DiseaseInsight(
            id = "campak",
            name = "Campak (Measles)",
            emoji = "🔴",
            imageBgColor = 0xFFD84315,
            keyFacts = listOf(
                "Ditandai dengan gejala ruam merah makulopapular di seluruh tubuh.",
                "Komplikasi berbahaya dapat memicu radang otak (ensefalitis) dan pneumonia.",
                "Dicegah melalui imunisasi kombinasi MR/MMR pada bayi."
            ),
            overview = "Penyakit infeksi virus akut yang sangat menular pada anak-anak melalui droplet pernapasan udara."
        ),
        DiseaseInsight(
            id = "difteri",
            name = "Difteri (Diphtheria)",
            emoji = "🗣️",
            imageBgColor = 0xFF37474F,
            keyFacts = listOf(
                "Membentuk selaput tebal abu-abu di tenggorokan yang menyumbat jalur napas.",
                "Bakterinya mengeluarkan toksin berbahaya yang merusak otot jantung.",
                "Dicegah dengan komponen rutin vaksin DPT-HB-Hib."
            ),
            overview = "Infeksi bakteri serius pada selaput lendir hidung dan tenggorokan yang berisiko fatal jika terlambat ditangani."
        )
    )
}