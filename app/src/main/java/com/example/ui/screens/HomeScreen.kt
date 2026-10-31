package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NewsItem
import com.example.data.model.PanchayatMember
import com.example.ui.theme.NezaiDarkBlue
import com.example.ui.theme.NezaiGreen
import com.example.ui.theme.NezaiSaffron
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.NezaiUiState
import com.example.ui.viewmodel.NezaiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    nezaiViewModel: NezaiViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentDetailScreen by remember { mutableStateOf<String?>(null) }
    val userProfile by nezaiViewModel.userProfile.collectAsState()
    val isAdmin = userProfile?.role == "admin"

    if (currentDetailScreen != null) {
        when (currentDetailScreen) {
            "Village Info" -> VillageInfoScreen(nezaiViewModel) { currentDetailScreen = null }
            "Contacts" -> ContactsScreen(nezaiViewModel) { currentDetailScreen = null }
            else -> currentDetailScreen = null
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_nezai_logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("NEZAI", fontWeight = FontWeight.Bold, color = NezaiDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = NezaiGreen)
                    }
                    IconButton(onClick = { /* TODO: Profile/Settings */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NezaiGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("Schemes") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Feedback, null) },
                    label = { Text("Complaints") }
                )
                if (isAdmin) {
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.AdminPanelSettings, null) },
                        label = { Text("Admin") }
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DashboardScreen(nezaiViewModel) { currentDetailScreen = it }
                1 -> SchemesScreen(nezaiViewModel)
                2 -> ComplaintsScreen(nezaiViewModel)
                3 -> AdminScreen(nezaiViewModel)
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: NezaiViewModel, onNavigate: (String) -> Unit) {
    val villageInfo by viewModel.villageInfo.collectAsState()
    val news by viewModel.news.collectAsState()
    val members by viewModel.panchayatMembers.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HeaderSection()
        }
        item {
            QuickActionsSection(onNavigate)
        }
        item {
            LatestNewsSection(news)
        }
        item {
            PanchayatSection(members)
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_village_cover),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                "Welcome to Giridharpur",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Giridharpur Gram Panchayat, Lakhisarai",
                color = NezaiSaffron,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun QuickActionsSection(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Quick Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            QuickActionButton("Village Info", Icons.Default.Info, NezaiGreen) { onNavigate("Village Info") }
            QuickActionButton("Complaints", Icons.Default.Feedback, NezaiSaffron) { /* Tab handles it */ }
            QuickActionButton("Govt Schemes", Icons.Default.Assignment, NezaiDarkBlue) { /* Tab handles it */ }
            QuickActionButton("Gallery", Icons.Default.Image, NezaiGreen) { onNavigate("Gallery") }
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            QuickActionButton("Contacts", Icons.Default.Phone, NezaiDarkBlue) { onNavigate("Contacts") }
            QuickActionButton("Development", Icons.Default.Engineering, NezaiSaffron) { onNavigate("Development") }
            QuickActionButton("Panchayat", Icons.Default.Groups, NezaiGreen) { onNavigate("Panchayat") }
            QuickActionButton("Map", Icons.Default.Map, NezaiDarkBlue) { onNavigate("Map") }
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.1f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun LatestNewsSection(newsState: NezaiUiState<List<NewsItem>>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Latest News", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("View All", color = NezaiGreen, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        when (newsState) {
            is NezaiUiState.Loading -> CircularProgressIndicator()
            is NezaiUiState.Success -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(newsState.data) { item ->
                        NewsCard(item)
                    }
                }
            }
            is NezaiUiState.Error -> Text("Error loading news")
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Placeholder or real image
            Box(modifier = Modifier.height(150.dp).fillMaxWidth().background(Color.LightGray)) {
                // If imageUrl is present, use Coil here. For now, just a placeholder.
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(news.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                Text(news.content, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PanchayatSection(membersState: NezaiUiState<List<PanchayatMember>>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Panchayat Representatives", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        when (membersState) {
            is NezaiUiState.Loading -> CircularProgressIndicator()
            is NezaiUiState.Success -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(membersState.data) { member ->
                        MemberCard(member)
                    }
                }
            }
            is NezaiUiState.Error -> Text("Error loading members")
        }
    }
}

@Composable
fun MemberCard(member: PanchayatMember) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.height(8.dp))
        Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(member.role, fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

// Screens are defined in SubScreens.kt and AdminScreen.kt
