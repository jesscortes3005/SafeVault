package com.example.safevault.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HomeCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotes: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnomaly: () -> Unit
) {
    val categories = remember {
        listOf(
            HomeCategory("Notas", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF60A5FA), onNavigateToNotes),
            HomeCategory("Galería", Icons.Default.Collections, Color(0xFF818CF8), onNavigateToGallery),
            HomeCategory("Documentos", Icons.Default.Folder, Color(0xFFFBBF24), onNavigateToDocuments),
            HomeCategory("Anomalías", Icons.Default.Security, Color(0xFFEF4444), onNavigateToAnomaly),
            HomeCategory("Historial", Icons.Default.WatchLater, Color(0xFFFDE047), onNavigateToHistory),
            HomeCategory("Ajustes", Icons.Default.Settings, Color(0xFF94A3B8), onNavigateToSettings)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "SAFE VAULT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 5.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bienvenido de nuevo,",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFFEAB308),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Administrador",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(categories) { index, category ->
                        AnimatedCategoryCard(index, category)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCategoryCard(index: Int, category: HomeCategory) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = index * 100)) +
                slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(600, delayMillis = index * 100)
                )
    ) {
        CategoryCard(category)
    }
}

@Composable
fun CategoryCard(category: HomeCategory) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable { category.onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
            )
        }
    }
}
