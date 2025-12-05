package com.example.notificacionesapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notificacionesapp.ui.components.*
import com.example.notificacionesapp.viewmodel.NotaViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.res.painterResource
import com.example.notificacionesapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, notaViewModel: NotaViewModel) {
    val todasNotas by notaViewModel.todasLasNotas.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }
    var isSearchActive by remember { mutableStateOf(false) }

    val notasFiltradas = remember(todasNotas, searchQuery, selectedFilter) {
        todasNotas.filter { nota ->
            val matchesSearch = searchQuery.isEmpty() ||
                    nota.titulo.contains(searchQuery, ignoreCase = true) ||
                    nota.descripcion.contains(searchQuery, ignoreCase = true) ||
                    nota.categoria.contains(searchQuery, ignoreCase = true) ||
                    nota.etiquetas.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Todas" -> true
                "Pendientes" -> !nota.completado
                "Completadas" -> nota.completado
                "Alta" -> nota.prioridad == "Alta"
                "Media" -> nota.prioridad == "Media"
                "Baja" -> nota.prioridad == "Baja"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isSearchActive) {
                        Text(text = "Mis Notas (${notasFiltradas.size})")
                    }
                },
                actions = {
                    if (isSearchActive) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                    }

                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            painter = painterResource(
                                id = if (isSearchActive) R.drawable.ic_close
                                else R.drawable.ic_search
                            ),
                            contentDescription = if (isSearchActive) "Cerrar búsqueda" else "Buscar"
                        )
                    }

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_more_vert),
                                contentDescription = "Opciones"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Backup y Restauración") },
                                onClick = {
                                    expanded = false
                                    navController.navigate("backup")
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_backup),
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Configuración") },
                                onClick = {
                                    expanded = false
                                    navController.navigate("configuracion")
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_settings),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("notaForm") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Nueva nota"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (!isSearchActive) {
                FilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when {
                notasFiltradas.isEmpty() && searchQuery.isNotEmpty() -> {
                    EmptySearchState(
                        searchQuery = searchQuery,
                        onClearSearch = {
                            searchQuery = ""
                            isSearchActive = false
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                notasFiltradas.isEmpty() -> {
                    EmptyState(
                        onAddNote = { navController.navigate("notaForm") },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    EnhancedListaNotas(notasFiltradas) { nota ->
                        navController.navigate("detalleNota/${nota.id}")
                    }
                }
            }
        }
    }
}


@Composable
fun EmptySearchState(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Para \"$searchQuery\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onClearSearch
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_clear),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Limpiar búsqueda")
        }
    }
}

@Composable
fun EnhancedListaNotas(
    notas: List<com.example.notificacionesapp.data.entities.Nota>,
    onNotaClick: (com.example.notificacionesapp.data.entities.Nota) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notas, key = { it.id }) { nota ->
            EnhancedNotaCard(
                nota = nota,
                onNotaClick = { onNotaClick(nota) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EnhancedNotaCard(
    nota: com.example.notificacionesapp.data.entities.Nota,
    onNotaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onNotaClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (nota.completado) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nota.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (nota.prioridad) {
                        "Alta" -> MaterialTheme.colorScheme.errorContainer
                        "Media" -> MaterialTheme.colorScheme.primaryContainer
                        "Baja" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when (nota.prioridad) {
                        "Alta" -> MaterialTheme.colorScheme.onErrorContainer
                        "Media" -> MaterialTheme.colorScheme.onPrimaryContainer
                        "Baja" -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ) {
                    Text(
                        text = nota.prioridad,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nota.descripcion.isNotEmpty()) {
                Text(
                    text = nota.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⏰ ${nota.hora}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (nota.categoria.isNotEmpty() && nota.categoria != "General") {
                        Text(
                            text = "📁 ${nota.categoria}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Usando drawables para íconos multimedia
                    if (!nota.imagenUri.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_photo),
                            contentDescription = "Tiene imagen",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (!nota.audioUri.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = "Tiene audio",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (!nota.videoUri.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_videocam),
                            contentDescription = "Tiene video",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (nota.completado) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = if (nota.completado) "✓" else "⏳",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (nota.completado) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            if (nota.etiquetas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    nota.etiquetas.split(",").forEach { tag ->
                        if (tag.trim().isNotEmpty()) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Text(
                                    text = "#${tag.trim()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}