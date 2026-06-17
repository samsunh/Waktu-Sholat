package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.PrayerLog
import com.example.data.local.PrayerSetting
import com.example.model.City
import com.example.model.INDONESIAN_CITIES
import com.example.model.Hadith
import com.example.ui.viewmodel.MainViewModel
import com.example.util.CompassManager
import com.example.util.SoundManager
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Dedicated Brand Colors
val EmeraldDark = Color(0xFF2D6A4F)
val EmeraldLight = Color(0xFF40916C)
val GoldAccent = Color(0xFFE5A93C)
val ClaySky = Color(0xFFF1F5EB)
val SandMedium = Color(0xFFDCE5D5)
val ProfessionalBg = Color(0xFFF7F9F2)
val BorderNeutral = Color(0xFFE1E4DC)
val TextPrimary = Color(0xFF11130F)
val TextSecondary = Color(0xFF424940)
val TextMuted = Color(0xFF72796F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    compassManager: CompassManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    
    val currentCityName by viewModel.currentCityName.collectAsStateWithLifecycle()
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val currentDateLogs by viewModel.currentDateLogs.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfessionalBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "Mosque Icon",
                            tint = GoldAccent,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(
                            text = "Sajadah",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = EmeraldDark
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(ClaySky)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Location Icon",
                                tint = EmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentCityName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ProfessionalBg,
                    titleContentColor = EmeraldDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Jadwal & Absensi"
                        )
                    },
                    label = { Text("Jadwal", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldLight,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = ClaySky,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_schedule")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.Explore else Icons.Outlined.Explore,
                            contentDescription = "Arah Kiblat"
                        )
                    },
                    label = { Text("Kiblat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldLight,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = ClaySky,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_qibla")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Tadabbur & Pengingat"
                        )
                    },
                    label = { Text("Tadabbur", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldLight,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = ClaySky,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(ProfessionalBg)
        ) {
            when (selectedTab) {
                0 -> ScheduleTab(viewModel = viewModel)
                1 -> QiblaTab(viewModel = viewModel, compassManager = compassManager)
                2 -> ReflectionAndSettingsTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ScheduleTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    var showCityDialog by remember { mutableStateOf(false) }
    
    val currentCityName by viewModel.currentCityName.collectAsStateWithLifecycle()
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val currentDateLogs by viewModel.currentDateLogs.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()

    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.requestGpsLocation(context)
                Toast.makeText(context, "Mencari koordinat GPS...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Izin lokasi ditolak. Silakan gunakan preset kota.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // Filter essential daily prayers
    val prayerNames = listOf("Subuh", "Syuruk", "Dzuhur", "Ashar", "Maghrib", "Isya")
    val checkedPrayersCount = currentDateLogs.filter { it.isCompleted && it.prayerName != "Syuruk" }.size
    // Total pray count is 5 (Syuruk is sunrise, not obligatory checklist prayer for absensi)
    val totalRequiredPrayCount = 5
    val currentDayProgress = if (totalRequiredPrayCount > 0) checkedPrayersCount.toFloat() / totalRequiredPrayCount else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("schedule_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Visual Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(EmeraldDark)
                    .border(BorderStroke(1.dp, BorderNeutral), RoundedCornerShape(32.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_islamic_banner),
                    contentDescription = "Spiritual Hero Art Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.4f
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Absensi Sholat Harian",
                            color = GoldAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Akurasi Waktu Sholat Adaptif Lokasi",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Tanggal: $currentDate",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aktif di: $currentCityName",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // GPS trigger
                            FilledIconButton(
                                onClick = {
                                    locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = GoldAccent,
                                    contentColor = EmeraldDark
                                ),
                                modifier = Modifier.size(36.dp).testTag("gps_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Use GPS Coordinates",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            // Map presets trigger
                            Button(
                                onClick = { showCityDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = EmeraldDark
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("change_city_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Cari Kota",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pilih Kota", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Attendance Progress Tracker Widget
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, BorderNeutral),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ClaySky),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { currentDayProgress },
                            color = EmeraldLight,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "$checkedPrayersCount/$totalRequiredPrayCount",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tingkat Kepatuhan Sholat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldDark
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = ClaySky)
                        Text(
                            text = if (checkedPrayersCount == totalRequiredPrayCount) 
                                "Maa Syaa Allah! Absensi hari ini lengkap." 
                                else "Mari disiplinkan shalat wajib tepat waktu harian.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // List Header
        item {
            Text(
                text = "Jadwal Shalat Hari Ini",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        // Checklist of Prayer Times
        items(prayerNames) { prayerName ->
            val prayerTime = prayerTimes[prayerName] ?: "--:--"
            val isCompleted = currentDateLogs.any { it.prayerName == prayerName && it.isCompleted }
            
            // Note: Syuruk is sunrise, not a daily mandatory checked-prayer
            val isCheckable = prayerName != "Syuruk"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isCheckable) {
                        viewModel.togglePrayerAttendance(prayerName)
                    }
                    .testTag("prayer_item_$prayerName"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) Color(0xFFF1F5EB) else Color.White
                ),
                border = if (isCompleted) {
                    BorderStroke(1.5.dp, EmeraldDark)
                } else {
                    BorderStroke(1.dp, BorderNeutral)
                },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isCompleted) EmeraldLight else Color(0xFFF5F5F5)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (prayerName) {
                                    "Subuh" -> Icons.Default.WbTwilight
                                    "Syuruk" -> Icons.Default.WbSunny
                                    "Dzuhur" -> Icons.Default.LightMode
                                    "Ashar" -> Icons.Default.WbCloudy
                                    "Maghrib" -> Icons.Default.NightlightRound
                                    else -> Icons.Default.Nightlight
                                },
                                contentDescription = "$prayerName Icon",
                                tint = if (isCompleted) Color.White else EmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = prayerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EmeraldDark
                            )
                            if (isCompleted) {
                                Text(
                                    text = "Sudah Absen",
                                    fontSize = 11.sp,
                                    color = EmeraldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else if (!isCheckable) {
                                Text(
                                    text = "Batas Terbit Fajar",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            } else {
                                Text(
                                    text = "Belum Tercatat",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = prayerTime,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) EmeraldDark else Color(0xFF333333),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        if (isCheckable) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = {
                                    viewModel.togglePrayerAttendance(prayerName)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldLight,
                                    checkmarkColor = Color.White
                                )
                            )
                        } else {
                            // Non-checking element placeholder for Syuruk
                            Box(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal popup to choose city presets
    if (showCityDialog) {
        Dialog(onDismissRequest = { showCityDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Pilih Lokasi Preset Kota",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EmeraldDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    HorizontalDivider(color = ClaySky)
                    
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(INDONESIAN_CITIES) { city ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.selectPresetCity(city)
                                            showCityDialog = false
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = city.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = EmeraldDark
                                        )
                                        Text(
                                            text = city.description,
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    if (city.name == currentCityName) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = EmeraldLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { showCityDialog = false },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Batal")
                    }
                }
            }
        }
    }
}

@Composable
fun QiblaTab(viewModel: MainViewModel, compassManager: CompassManager) {
    val latitude by viewModel.latitude.collectAsStateWithLifecycle()
    val longitude by viewModel.longitude.collectAsStateWithLifecycle()
    val cityBySetting by viewModel.currentCityName.collectAsStateWithLifecycle()

    // Real-time sensor-based compass flow collected directly
    val compassHeading by remember { compassManager.getHeadingFlow() }.collectAsState(initial = 0f)

    // Compute Qibla astronomical direction details
    val qiblaBearing = remember(latitude, longitude) {
        CompassManager.calculateQiblaDirection(latitude, longitude)
    }

    // Relative rotation error mapping representing orientation deviation
    val angleDifference = remember(qiblaBearing, compassHeading) {
        val diff = abs(qiblaBearing - compassHeading)
        if (diff > 180) 360 - diff else diff
    }

    val isAligned = angleDifference < 4.0 // accurate alignment trigger

    val alignmentColor by animateColorAsState(
        targetValue = if (isAligned) Color(0xFF2E7D32) else GoldAccent,
        animationSpec = tween(durationMillis = 300),
        label = "alignment_glowing_state"
    )

    val scaleNeedle by animateFloatAsState(
        targetValue = if (isAligned) 1.05f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "aligned_scale_bounce"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("qibla_root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper card status details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, BorderNeutral),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KOMPAS KIBLAT DIGITAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Akurasi Sudut Terintegrasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = ClaySky)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lokasi Sekarang", fontSize = 10.sp, color = Color.Gray)
                        Text(cityBySetting, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hadap HP", fontSize = 10.sp, color = Color.Gray)
                        Text("${compassHeading.toInt()}°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Derajat Kiblat", fontSize = 10.sp, color = Color.Gray)
                        Text("%.1f°".format(qiblaBearing), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                    }
                }
            }
        }

        // Animated Compass Rendering Canvas Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, ClaySky), CircleShape)
                    .padding(12.dp)
            ) {
                // Background rotating dial representing real magnetic North alignment
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(-compassHeading)
                ) {
                    val rCenter = Offset(size.width / 2, size.height / 2)
                    val rRadius = min(size.width, size.height) / 2

                    // Major Compass points
                    val points = listOf("U" to 0f, "T" to 90f, "S" to 180f, "B" to 270f)
                    points.forEach { (label, degree) ->
                        val rad = Math.toRadians((degree - 90).toDouble())
                        val px = (rCenter.x + (rRadius - 18.dp.toPx()) * cos(rad)).toFloat()
                        val py = (rCenter.y + (rRadius - 18.dp.toPx()) * sin(rad)).toFloat()
                        
                        // Draw tiny orientation tags
                        // Rotate draw logic so letter sits straight up
                    }
                }

                // Beautiful interactive Vector drawing canvas representing Qibla direction and North
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = min(size.width, size.height) / 2

                    // Draw outer border reference
                    drawCircle(
                        color = ClaySky,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Draw central structural compass circle
                    drawCircle(
                        color = Color(0xFFFAFAFA),
                        radius = radius * 0.4f,
                        center = center
                    )

                    // Compass Dial Tick Drawing rotated matching device North
                    rotate(degrees = -compassHeading, pivot = center) {
                        // Drawing static North marker red triangle
                        val pathNorth = androidx.compose.ui.graphics.Path().apply {
                            moveTo(center.x, center.y - radius + 8.dp.toPx())
                            lineTo(center.x - 6.dp.toPx(), center.y - radius + 18.dp.toPx())
                            lineTo(center.x + 6.dp.toPx(), center.y - radius + 18.dp.toPx())
                            close()
                        }
                        drawPath(pathNorth, color = Color(0xFFC62828))
                        
                        // Ticks matching major bearings
                        for (i in 0 until 360 step 30) {
                            rotate(degrees = i.toFloat(), pivot = center) {
                                drawLine(
                                    color = if (i == 0) Color(0xFFC62828) else Color.LightGray,
                                    start = Offset(center.x, center.y - radius + 4.dp.toPx()),
                                    end = Offset(center.x, center.y - radius + 12.dp.toPx()),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }
                    }

                    // Rotated Golden Aligned Qibla Arrow Pointer Needle
                    val relativeQiblaRotation = (qiblaBearing - compassHeading).toFloat()
                    rotate(degrees = relativeQiblaRotation, pivot = center) {
                        // Gold main pointer needle line representation
                        drawLine(
                            color = alignmentColor,
                            start = center,
                            end = Offset(center.x, center.y - radius + 22.dp.toPx()),
                            strokeWidth = 6.dp.toPx() * scaleNeedle,
                            cap = StrokeCap.Round
                        )
                        
                        // Kabah icon/house tip representation
                        val iconRadius = 14.dp.toPx()
                        val housePath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(center.x, center.y - radius + 15.dp.toPx())
                            lineTo(center.x - 10.dp.toPx(), center.y - radius + 32.dp.toPx())
                            lineTo(center.x + 10.dp.toPx(), center.y - radius + 32.dp.toPx())
                            close()
                        }
                        drawPath(housePath, color = alignmentColor)
                    }
                    
                    // Static Phone Orientation Centerline targeting straight ahead
                    drawLine(
                        color = EmeraldDark.copy(alpha = 0.5f),
                        start = center,
                        end = Offset(center.x, center.y - radius - 5.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f), 0f
                        )
                    )
                }
            }
        }

        // Live status feedback card representing precise alignment state
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAligned) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (isAligned) Color(0xFF81C784) else BorderNeutral
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = "Status",
                    tint = if (isAligned) Color(0xFF2E7D32) else GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isAligned) 
                        "Alhamdulillah, HP Sejajar! Silakan Memulai Sholat."
                        else "Sejajarkan HP Anda dengan menyejajarkan kubah emas ke arah garis depan.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAligned) Color(0xFF1B5E20) else Color(0xFF5D4037),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ReflectionAndSettingsTab(viewModel: MainViewModel) {
    val selectedHadith by viewModel.selectedHadith.collectAsStateWithLifecycle()
    val aiExplanation by viewModel.aiExplanation.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
    
    val coroutineScope = rememberCoroutineScope()
    var isTestingAudio by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reflection_settings_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HADITH SECTION
        item {
            Text(
                text = "Hadits Harian & Tadabbur AI",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, BorderNeutral),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Badge topic
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClaySky)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Topik: ${selectedHadith.topic}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Arabic
                    Text(
                        text = selectedHadith.arabic,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Translation
                    Text(
                        text = "\"${selectedHadith.indonesian}\"",
                        fontSize = 13.sp,
                        color = Color(0xFF333333),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Left
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Narrator / Referencer
                    Text(
                        text = "${selectedHadith.narrator} (${selectedHadith.book})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        textAlign = TextAlign.Left
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(color = ClaySky)
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Action controllers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.nextHadith() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldLight),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, EmeraldLight),
                            modifier = Modifier.testTag("next_hadith_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Siklus", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hadits Lain")
                        }

                        Button(
                            onClick = { viewModel.askGeminiForTadabbur() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldLight,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.testTag("ai_tadabbur_button"),
                            enabled = !aiLoading
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Tanya AI", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tadabbur AI")
                        }
                    }
                }
            }
        }

        // Live sliding/loading Gemini Response Card
        if (aiLoading || aiExplanation != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ClaySky),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(1.dp, BorderNeutral)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini",
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Penjelasan Tadabbur AI (Gemini Flash)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldLight
                            )
                        }

                        if (aiLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = EmeraldLight,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Memformulasikan Hikmah Spiritual...",
                                        fontSize = 12.sp,
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = aiExplanation ?: "",
                                fontSize = 13.sp,
                                color = EmeraldDark,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // PERSONALIZATION NOTIFICATIONS & AUDIO TEST SECTION
        item {
            Text(
                text = "Kustomisasi Pengingat & Suara Adzan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, BorderNeutral),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Suara Bell & Melodi Synthesizer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldDark
                    )
                    Text(
                        text = "Aparatus bell akustik spiritual harian yang berbunyi tepat sebagai pertanda.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Button(
                        onClick = {
                            if (isTestingAudio) {
                                SoundManager.stopSound()
                                isTestingAudio = false
                            } else {
                                isTestingAudio = true
                                coroutineScope.launch {
                                    SoundManager.playIslamicChime()
                                    isTestingAudio = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTestingAudio) Color(0xFFC62828) else EmeraldLight,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().testTag("play_sound_test_button")
                    ) {
                        Icon(
                            imageVector = if (isTestingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Test Audio"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTestingAudio) "Hentikan Bunyi Chime" else "Uji Bunyi Melodi Chime")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = ClaySky)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Preferensi Berdasar Waktu Shalat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Table items mapping dynamic configurations
                    val prayersAndSettings = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
                    prayersAndSettings.forEach { prayer ->
                        val currentConfig = settingsState.find { it.prayerName == prayer } 
                            ?: PrayerSetting(prayerName = prayer, isReminderEnabled = true, notificationType = "ADZAN")

                        val soundOptions = listOf("ADZAN" to "Chime Melodi Syahdu", "NOTIFIKASI" to "Sistem Alarm", "SENYAP" to "Senyap/Getar Saja")
                        var expandedDropdown by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (currentConfig.isReminderEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = "Alert",
                                    tint = if (currentConfig.isReminderEnabled) GoldAccent else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = prayer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDark
                                    )
                                    Box {
                                        Text(
                                            text = when (currentConfig.notificationType) {
                                                "ADZAN" -> "🔔 Chime Melodi Syahdu"
                                                "NOTIFIKASI" -> "🔕 Bunyi Alarm Sistem"
                                                else -> "🤫 Getar / Senyap"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldLight,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ClaySky)
                                                .clickable { expandedDropdown = true }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )

                                        DropdownMenu(
                                            expanded = expandedDropdown,
                                            onDismissRequest = { expandedDropdown = false },
                                            modifier = Modifier.background(Color.White)
                                        ) {
                                            soundOptions.forEach { (typeVal, labelText) ->
                                                DropdownMenuItem(
                                                    text = { Text(labelText, fontSize = 13.sp) },
                                                    onClick = {
                                                        viewModel.updateNotificationSetting(
                                                            currentConfig.copy(notificationType = typeVal)
                                                        )
                                                        expandedDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Enable toggle Switch
                            Switch(
                                checked = currentConfig.isReminderEnabled,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateNotificationSetting(
                                        currentConfig.copy(isReminderEnabled = isChecked)
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = EmeraldLight,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
