package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

enum class AppScreen {
    STUDIO,
    CLONES,
    PROJECTS,
    TEMPLATES
}

class MainActivity : ComponentActivity() {

    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: StudioViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.STUDIO) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF241C3C),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = StudioDarkBg,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.STUDIO,
                    onClick = { currentScreen = AppScreen.STUDIO },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.STUDIO) Icons.Filled.GraphicEq else Icons.Outlined.GraphicEq,
                            contentDescription = "Studio"
                        )
                    },
                    label = { Text("Studio", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.STUDIO) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan.copy(alpha = 0.15f),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.CLONES,
                    onClick = { currentScreen = AppScreen.CLONES },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.CLONES) Icons.Filled.Mic else Icons.Outlined.Mic,
                            contentDescription = "Voice Clone"
                        )
                    },
                    label = { Text("Clones", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.CLONES) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCoral,
                        selectedTextColor = NeonCoral,
                        indicatorColor = NeonCoral.copy(alpha = 0.15f),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.TEMPLATES,
                    onClick = { currentScreen = AppScreen.TEMPLATES },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.TEMPLATES) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Templates"
                        )
                    },
                    label = { Text("Templates", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.TEMPLATES) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent.copy(alpha = 0.15f),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.PROJECTS,
                    onClick = { currentScreen = AppScreen.PROJECTS },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.PROJECTS) Icons.Filled.Folder else Icons.Outlined.Folder,
                            contentDescription = "Projects"
                        )
                    },
                    label = { Text("Projects", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.PROJECTS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricPurpleLight,
                        selectedTextColor = ElectricPurpleLight,
                        indicatorColor = ElectricPurple.copy(alpha = 0.15f),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )
            }
        },
        containerColor = StudioDarkBg,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.STUDIO -> {
                    StudioScreen(
                        viewModel = viewModel,
                        onNavigateToClones = { currentScreen = AppScreen.CLONES },
                        onNavigateToProjects = { currentScreen = AppScreen.PROJECTS },
                        onNavigateToTemplates = { currentScreen = AppScreen.TEMPLATES }
                    )
                }
                AppScreen.CLONES -> {
                    VoiceCloneScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STUDIO }
                    )
                }
                AppScreen.TEMPLATES -> {
                    TemplatesScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STUDIO },
                        onTemplateLoaded = { currentScreen = AppScreen.STUDIO }
                    )
                }
                AppScreen.PROJECTS -> {
                    ProjectsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STUDIO },
                        onOpenInStudio = { currentScreen = AppScreen.STUDIO }
                    )
                }
            }
        }
    }
}
