package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FemTechCycleData
import com.example.model.Gender
import com.example.model.MaternalHealthScreening
import com.example.model.PregnancyTrackerData
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.IceMint
import com.example.ui.theme.LifeSaversCoral
import com.example.ui.theme.LifeSaversTeal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmBeige
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * UNICEF FemTech Innovation Suite for LifeSavers:
 * 1. Reproductive Cycle & Ovulation History Calculator
 * 2. Maternal Continuum of Care & 1,000 Days Pregnancy Milestone Tracker
 * 3. Point-of-Care Anaemia & Maternal Vitals Risk Screening
 * 4. Safety-by-Design On-Device Privacy & Consent Shield
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FemTechHealthScreen(
    cycleData: FemTechCycleData,
    pregnancyData: PregnancyTrackerData,
    screenings: List<MaternalHealthScreening>,
    onUpdateCycleData: (FemTechCycleData) -> Unit,
    onRequestMaternalDispatch: () -> Unit,
    onBack: () -> Unit,
    userGender: Gender = Gender.FEMALE,
    isProvider: Boolean = false,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = if (isProvider) {
        listOf("Point-of-Care Vitals & Triage", "Privacy & Safety Shield")
    } else if (userGender == Gender.MALE) {
        listOf("Prostate & Urinary", "Male Fertility")
    } else {
        listOf("Cycle & Fertility", "Maternal Care (1,000 Days)")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isProvider) Icons.Default.MedicalServices else if (userGender == Gender.MALE) Icons.Default.Male else Icons.Default.Female,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isProvider) "Specialist Point-of-Care Suite" else if (userGender == Gender.MALE) "LifeSavers Men's Health Suite" else "LifeSavers FemTech Suite",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PureWhite
                            )
                            Text(
                                text = if (isProvider) "Vitals Triage & On-Device Consent Shield" else if (userGender == Gender.MALE) "Prostate, Fertility, Hormonal Vitality & SRH" else "Reproductive & Maternal Health Continuum",
                                fontSize = 11.sp,
                                color = IceMint
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ForestGreen)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (tabs.size > 1) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = ForestGreen,
                    contentColor = PureWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AmberAccent,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) AmberAccent else IceMint
                                )
                            }
                        )
                    }
                }
            }

            when {
                isProvider -> {
                    when (selectedTabIndex) {
                        0 -> PointOfCareAndPrivacyView(
                            screenings = screenings,
                            cycleData = cycleData,
                            onUpdateCycleData = onUpdateCycleData,
                            onRequestMaternalDispatch = onRequestMaternalDispatch
                        )
                        else -> PointOfCareAndPrivacyView(
                            screenings = screenings,
                            cycleData = cycleData,
                            onUpdateCycleData = onUpdateCycleData,
                            onRequestMaternalDispatch = onRequestMaternalDispatch
                        )
                    }
                }
                userGender == Gender.MALE -> {
                    when (selectedTabIndex) {
                        0 -> MaleProstateAndUrinaryView(onRequestAndrologistDispatch = onRequestMaternalDispatch)
                        else -> MaleFertilityAndVitalityView(onRequestAndrologistDispatch = onRequestMaternalDispatch)
                    }
                }
                else -> {
                    when (selectedTabIndex) {
                        0 -> CycleAndFertilityView(
                            cycleData = cycleData,
                            onUpdateCycleData = onUpdateCycleData
                        )
                        else -> MaternalCareView(
                            pregnancyData = pregnancyData,
                            onRequestMaternalDispatch = onRequestMaternalDispatch
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CycleAndFertilityView(
    cycleData: FemTechCycleData,
    onUpdateCycleData: (FemTechCycleData) -> Unit
) {
    var lastPeriodDateStr by remember { mutableStateOf(cycleData.lastPeriodStartDate) }
    var cycleLengthStr by remember { mutableStateOf(cycleData.cycleLengthDays.toString()) }
    var periodLengthStr by remember { mutableStateOf(cycleData.periodLengthDays.toString()) }
    var symptomsList by remember { mutableStateOf(cycleData.loggedSymptoms.toMutableList()) }

    // Live Date Math Calculations
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val parsedLastDate = remember(lastPeriodDateStr) {
        runCatching { dateFormat.parse(lastPeriodDateStr) }.getOrNull() ?: Date()
    }
    val cycleLengthDays = cycleLengthStr.toIntOrNull()?.coerceIn(20, 45) ?: 28
    val periodLengthDays = periodLengthStr.toIntOrNull()?.coerceIn(2, 10) ?: 5

    val nextPeriodFormatted = remember(parsedLastDate, cycleLengthDays) {
        val cal = Calendar.getInstance().apply {
            time = parsedLastDate
            add(Calendar.DAY_OF_YEAR, cycleLengthDays)
        }
        dateFormat.format(cal.time)
    }

    val ovulationFormatted = remember(parsedLastDate, cycleLengthDays) {
        val cal = Calendar.getInstance().apply {
            time = parsedLastDate
            add(Calendar.DAY_OF_YEAR, (cycleLengthDays - 14).coerceAtLeast(7))
        }
        dateFormat.format(cal.time)
    }

    val fertileWindowFormatted = remember(parsedLastDate, cycleLengthDays) {
        val calStart = Calendar.getInstance().apply {
            time = parsedLastDate
            add(Calendar.DAY_OF_YEAR, (cycleLengthDays - 18).coerceAtLeast(5))
        }
        val calEnd = Calendar.getInstance().apply {
            time = parsedLastDate
            add(Calendar.DAY_OF_YEAR, (cycleLengthDays - 11).coerceAtLeast(10))
        }
        "${dateFormat.format(calStart.time)} to ${dateFormat.format(calEnd.time)}"
    }

    val availableSymptoms = listOf(
        "Mild Cramps", "Normal Flow", "Headache", "Spotting",
        "Mood Swings", "Bloating", "Good Energy", "Fatigue", "Breast Tenderness"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarmBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Menstrual & Ovulation Cycle Calculator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Real-time cycle history calculator, ovulation window estimator, and symptom logger.",
                        fontSize = 12.sp,
                        color = DarkCharcoal.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Live Inputs & Controls
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cycle Parameters (Editable)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = lastPeriodDateStr,
                        onValueChange = { input ->
                            lastPeriodDateStr = input
                            onUpdateCycleData(
                                cycleData.copy(
                                    lastPeriodStartDate = input,
                                    nextPeriodDate = nextPeriodFormatted,
                                    estimatedOvulationDate = ovulationFormatted
                                )
                            )
                        },
                        label = { Text("Last Period Start Date (YYYY-MM-DD)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("last_period_date_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = cycleLengthStr,
                            onValueChange = { input ->
                                cycleLengthStr = input
                                val cVal = input.toIntOrNull() ?: 28
                                onUpdateCycleData(
                                    cycleData.copy(
                                        cycleLengthDays = cVal,
                                        nextPeriodDate = nextPeriodFormatted,
                                        estimatedOvulationDate = ovulationFormatted
                                    )
                                )
                            },
                            label = { Text("Cycle Length (Days)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = periodLengthStr,
                            onValueChange = { input ->
                                periodLengthStr = input
                                val pVal = input.toIntOrNull() ?: 5
                                onUpdateCycleData(cycleData.copy(periodLengthDays = pVal))
                            },
                            label = { Text("Period Duration (Days)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Live Dynamic Forecast Results
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Calculated Live Forecast",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ForecastTile(
                            title = "Next Expected Cycle",
                            value = nextPeriodFormatted,
                            icon = Icons.Default.CalendarMonth,
                            color = LifeSaversTeal,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        ForecastTile(
                            title = "Estimated Ovulation",
                            value = ovulationFormatted,
                            icon = Icons.Default.Favorite,
                            color = LifeSaversCoral,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IceMint,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Estimated Fertile Window",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = fertileWindowFormatted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Symptom Logger
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Symptom Logger (Tap to Toggle)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableSymptoms.forEach { symptom ->
                            val isSelected = symptomsList.contains(symptom)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) ForestGreen else WarmBeige,
                                modifier = Modifier.clickable {
                                    if (isSelected) {
                                        symptomsList.remove(symptom)
                                    } else {
                                        symptomsList.add(symptom)
                                    }
                                    onUpdateCycleData(cycleData.copy(loggedSymptoms = symptomsList.toList()))
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PureWhite,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = symptom,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PureWhite else DarkCharcoal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaternalCareView(
    pregnancyData: PregnancyTrackerData,
    onRequestMaternalDispatch: () -> Unit
) {
    var lmpDateStr by remember { mutableStateOf("2026-05-18") }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val parsedLmp = remember(lmpDateStr) {
        runCatching { dateFormat.parse(lmpDateStr) }.getOrNull() ?: Date()
    }

    val eddFormatted = remember(parsedLmp) {
        val cal = Calendar.getInstance().apply {
            time = parsedLmp
            add(Calendar.DAY_OF_YEAR, 280) // Naegele's rule: LMP + 280 days
        }
        dateFormat.format(cal.time)
    }

    val calculatedGestationWeeks = remember(parsedLmp) {
        val now = Date()
        val diffMs = now.time - parsedLmp.time
        val days = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
        (days / 7).toInt().coerceIn(1, 42)
    }

    val calculatedTrimester = when {
        calculatedGestationWeeks <= 12 -> 1
        calculatedGestationWeeks <= 27 -> 2
        else -> 3
    }

    val babySizeMilestone = when (calculatedGestationWeeks) {
        in 1..8 -> "Size of a Raspberry (~1.5 cm)"
        in 9..14 -> "Size of a Lemon (~8.5 cm)"
        in 15..22 -> "Size of a Sweet Potato (~14 cm)"
        in 23..28 -> "Size of an Ear of Corn (~36 cm)"
        in 29..35 -> "Size of a Squash (~45 cm)"
        else -> "Size of a Watermelon (~50 cm)"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1,000 Days Continuum Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChildCare,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Maternal Continuum of Care (First 1,000 Days)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "Antenatal tracker from conception through infant development.",
                                fontSize = 11.sp,
                                color = IceMint
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Gestation", fontSize = 11.sp, color = IceMint)
                            Text(
                                "$calculatedGestationWeeks Weeks",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Calculated Due Date (EDD)", fontSize = 11.sp, color = IceMint)
                            Text(
                                eddFormatted,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PureWhite.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👶 Fetal Milestone: $babySizeMilestone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }
            }
        }

        // Editable Last Menstrual Period Date for Pregnancy
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Antenatal LMP Input Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = lmpDateStr,
                        onValueChange = { lmpDateStr = it },
                        label = { Text("Last Menstrual Period Date (YYYY-MM-DD)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Clinical Milestones Checklist
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trimester $calculatedTrimester Antenatal Care Checklist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val milestones = when (calculatedTrimester) {
                        1 -> listOf("Folic acid & iron supplementation", "Initial maternal blood pressure baseline", "Routine ultrasound scan")
                        2 -> listOf("Fetal heart rate monitoring (145 bpm)", "Anatomy ultrasound scan", "Screening for pregnancy-induced hypertension")
                        else -> listOf("Birth plan preparation", "GBS screening & kick counter", "Antenatal hospital bag readiness")
                    }

                    milestones.forEach { milestone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = milestone,
                                fontSize = 13.sp,
                                color = DarkCharcoal
                            )
                        }
                    }
                }
            }
        }

        // Midwife & Antenatal Home Care Dispatch CTA
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarmBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Need Doorstep Antenatal Visit?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkCharcoal
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Dispatch a verified Nurse Midwife or Doctor to your doorstep for fetal heart monitoring, blood pressure checks, and prenatal vitamins.",
                        fontSize = 12.sp,
                        color = DarkCharcoal.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRequestMaternalDispatch,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dispatch_midwife_button")
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dispatch Midwife / Antenatal Specialist", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PointOfCareAndPrivacyView(
    screenings: List<MaternalHealthScreening>,
    cycleData: FemTechCycleData,
    onUpdateCycleData: (FemTechCycleData) -> Unit,
    onRequestMaternalDispatch: () -> Unit
) {
    var logsList by remember { mutableStateOf(screenings.toMutableList()) }
    var inputHb by remember { mutableStateOf("11.8") }
    var inputSys by remember { mutableStateOf("118") }
    var inputDia by remember { mutableStateOf("78") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Privacy & Safety-by-Design Shield
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Safety-by-Design Privacy Shield",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DarkCharcoal
                            )
                        }
                        Switch(
                            checked = cycleData.privacyConsentEnabled,
                            onCheckedChange = { isEnabled ->
                                onUpdateCycleData(cycleData.copy(privacyConsentEnabled = isEnabled))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = IceMint)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Encrypted on-device records adhering to UNICEF safety-by-design standards. Sensitive reproductive data is never shared without explicit consent.",
                        fontSize = 11.sp,
                        color = DarkCharcoal.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Add Point-of-Care Vitals Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Record Point-of-Care Maternal Screening",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputHb,
                            onValueChange = { inputHb = it },
                            label = { Text("Hb (g/dL)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputSys,
                            onValueChange = { inputSys = it },
                            label = { Text("Sys (mmHg)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputDia,
                            onValueChange = { inputDia = it },
                            label = { Text("Dia (mmHg)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val hb = inputHb.toFloatOrNull() ?: 11.5f
                            val sys = inputSys.toIntOrNull() ?: 120
                            val dia = inputDia.toIntOrNull() ?: 80

                            val risk = when {
                                sys >= 140 || dia >= 90 -> "HIGH_BLOOD_PRESSURE"
                                hb < 11.0f -> "MILD_ANEMIA"
                                else -> "NORMAL_LOW_RISK"
                            }

                            val action = when (risk) {
                                "HIGH_BLOOD_PRESSURE" -> "Pre-eclampsia risk flagged. Urgent Midwife consultation advised."
                                "MILD_ANEMIA" -> "Hemoglobin low. Increased dietary iron & folate recommended."
                                else -> "Vitals within normal physiological range."
                            }

                            val newLog = MaternalHealthScreening(
                                id = "scr-${System.currentTimeMillis() % 10000}",
                                dateText = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                hemoglobinLevelGdl = hb,
                                bloodPressureSys = sys,
                                bloodPressureDia = dia,
                                ironSupplementTaken = true,
                                riskAssessmentLevel = risk,
                                recommendedAction = action
                            )

                            logsList.add(0, newLog)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Screening Vitals", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Log Entries
        item {
            Text(
                text = "Point-of-Care Screening History",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ForestGreen
            )
        }

        items(logsList.size, key = { index -> logsList[index].id }) { index ->
            val screening = logsList[index]
            val isHighRisk = screening.riskAssessmentLevel == "HIGH_BLOOD_PRESSURE" || screening.riskAssessmentLevel == "MILD_ANEMIA"

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = if (isHighRisk) LifeSaversCoral else ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hemoglobin: ${screening.hemoglobinLevelGdl} g/dL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DarkCharcoal
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = WarmBeige
                        ) {
                            Text(
                                text = screening.dateText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Blood Pressure: ${screening.bloodPressureSys}/${screening.bloodPressureDia} mmHg • Iron Supplement: ${if (screening.ironSupplementTaken) "Yes" else "No"}",
                        fontSize = 12.sp,
                        color = DarkCharcoal.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isHighRisk) WarmBeige else IceMint
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (isHighRisk) LifeSaversCoral else ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Action: ${screening.recommendedAction}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighRisk) DarkCharcoal else ForestGreen
                            )
                        }
                    }

                    if (isHighRisk) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onRequestMaternalDispatch,
                            colors = ButtonDefaults.buttonColors(containerColor = LifeSaversCoral),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = DarkCharcoal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dispatch Health Worker to Doorstep", color = DarkCharcoal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastTile(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = WarmBeige,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 10.sp, color = DarkCharcoal.copy(alpha = 0.7f))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
        }
    }
}

@Composable
private fun MaleProstateAndUrinaryView(
    onRequestAndrologistDispatch: () -> Unit
) {
    var nocturiaFrequency by remember { mutableStateOf("0-1 times") }
    var streamWeakness by remember { mutableStateOf("Mild") }

    val isHighRisk = nocturiaFrequency.contains("3") || streamWeakness.contains("Severe")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarmBeige),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Male,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prostate Health & Urinary Flow Index (IPSS)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarkCharcoal
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Confidential International Prostate Symptom Score self-assessment for early detection of benign prostatic hyperplasia (BPH).",
                        fontSize = 12.sp,
                        color = DarkCharcoal.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nighttime Urination Frequency (Nocturia)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("0-1 times", "2 times", "3+ times").forEach { option ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (nocturiaFrequency == option) ForestGreen else IceMint,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { nocturiaFrequency = option }
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (nocturiaFrequency == option) PureWhite else DarkCharcoal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Stream Force & Weakness",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Normal", "Mild", "Severe").forEach { option ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (streamWeakness == option) ForestGreen else IceMint,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { streamWeakness = option }
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (streamWeakness == option) PureWhite else DarkCharcoal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isHighRisk) LifeSaversCoral else IceMint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHighRisk) "⚠️ Moderate/High Prostate Flow Triage Flagged" else "✅ Prostate Health Score: Normal Range",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHighRisk)
                            "Urinary symptoms indicate potential prostate enlargement. A confidential PSA blood test and Urologist/Andrologist review is recommended."
                        else
                            "No significant urinary flow obstruction flagged. Maintain annual PSA screening after age 40.",
                        fontSize = 12.sp,
                        color = DarkCharcoal.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRequestAndrologistDispatch,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dispatch Andrologist / Urologist Specialist", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MaleFertilityAndVitalityView(
    onRequestAndrologistDispatch: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Male Fertility & Sperm Health Guide",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "Seminal parameters, motility metrics & testosterone vitality",
                                fontSize = 11.sp,
                                color = IceMint
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WHO Male Semen Parameter Standards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        "Sperm Concentration" to "≥ 15 million / mL",
                        "Total Progressive Motility" to "≥ 40% active movement",
                        "Normal Morphology" to "≥ 4% normal form",
                        "Total Ejaculate Volume" to "≥ 1.5 mL"
                    ).forEach { (param, standard) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(param, fontSize = 12.sp, color = DarkCharcoal)
                            Text(standard, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onRequestAndrologistDispatch,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Book Confidential Semen Analysis & Fertility Review", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}
