@file:OptIn(ExperimentalMaterial3Api::class)
package fr.math.vikapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.math.vikapp.R
import fr.math.vikapp.data.*

@Composable
fun HomeScreen(
    viewModel: VikViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToRaids: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Vik'App",
                    modifier = Modifier.height(60.dp),
                    contentScale = ContentScale.Fit
                )
                
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Paramètres")
                }
            }
        }

        item {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Bienvenue sur Vik'App",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "L'expertise de l'orientation au service de votre performance.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Image(
                    painter = painterResource(id = R.drawable.raid),
                    contentDescription = "Raid illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Conçue exclusivement pour la communauté Vik'azim, Vik'App est la plateforme numérique de référence...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateToRaids,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008000)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Voir l'ensemble des prochains raids", color = Color.White)
                }
            }
        }

        item {
            Column {
                Text(
                    text = "Prochains raids",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF008000))
                } else if (viewModel.raids.isEmpty()) {
                    Text("Aucun raid disponible pour le moment.")
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.raids.take(3)) { raid ->
                            RaidCard(raid, onClick = { onNavigateToDetail(raid.id) })
                        }
                    }
                }
            }
        }
        
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.finish),
                    contentDescription = "Finish line",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {}
                Text(
                    text = "Prêt pour le défi ?",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
fun RaidListScreen(
    viewModel: VikViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tous les Raids") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barre de recherche
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.filterRaids(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Rechercher un raid (lieu, nom...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.filterRaids("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Effacer")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Simulation de recherche par distance
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Recherche à proximité (km)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF008000))
                }
            } else if (viewModel.filteredRaids.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun raid ne correspond à votre recherche.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.filteredRaids) { raid ->
                        RaidListItem(raid, onClick = { onNavigateToDetail(raid.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun RaidListItem(raid: Raid, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = raid.picture,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.raid_thumbnail),
                error = painterResource(id = R.drawable.raid_thumbnail)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = raid.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                raid.place?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                raid.dates?.start?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF008000),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun RaidDetailScreen(
    raidId: Int,
    viewModel: VikViewModel,
    onBack: () -> Unit
) {
    val raid = viewModel.raids.find { it.id == raidId }
    
    LaunchedEffect(raidId) {
        viewModel.fetchRacesForRaid(raidId)
    }

    if (raid == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Raid non trouvé")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(raid.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = raid.picture,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.raid_thumbnail),
                error = painterResource(id = R.drawable.raid_thumbnail)
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = raid.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow(icon = Icons.Default.DateRange, label = "Dates", value = "${raid.dates?.start ?: ""} - ${raid.dates?.end ?: ""}")
                InfoRow(icon = Icons.Default.LocationOn, label = "Lieu", value = raid.place ?: "Non spécifié")
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(text = "Liste des courses", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (viewModel.isLoadingRaces) {
                    CircularProgressIndicator(color = Color(0xFF008000))
                } else if (viewModel.races.isEmpty()) {
                    Text("Aucune course disponible for ce raid.")
                } else {
                    viewModel.races.forEach { race ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = race.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${race.distance ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${race.elevation ?: "N/A"}m", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (race.start_time != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Départ : ${race.start_time}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (race.price != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Prix repas : ${race.price}€", style = MaterialTheme.typography.bodySmall, color = Color(0xFF008000), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(text = "Informations complémentaires", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Âge minimum : ${raid.min_age ?: "N/A"}")
                Text("Nombre de courses : ${raid.races_count ?: "N/A"}")
                
                if (raid.registration?.start != null || raid.registration?.end != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Inscriptions", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text("Du ${raid.registration?.start ?: ""} au ${raid.registration?.end ?: ""}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF008000), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label : ", fontWeight = FontWeight.Medium)
        Text(text = value, color = Color.Gray)
    }
}

@Composable
fun SettingsScreen(viewModel: VikViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        HorizontalDivider()
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mode Sombre")
            Switch(
                checked = viewModel.isDarkMode,
                onCheckedChange = { viewModel.isDarkMode = it }
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notifications")
            Switch(
                checked = viewModel.notificationsEnabled,
                onCheckedChange = { viewModel.notificationsEnabled = it }
            )
        }
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Langue")
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth().clickable { expanded = true }.padding(vertical = 8.dp)) {
                Text(text = viewModel.language, color = Color.Gray)
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Français") },
                        onClick = { viewModel.language = "Français"; expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("English") },
                        onClick = { viewModel.language = "English"; expanded = false }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Version 1.0.0",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun RaidCard(raid: Raid, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(250.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = raid.picture,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.raid_thumbnail),
                    error = painterResource(id = R.drawable.raid_thumbnail)
                )
                
                if (raid.countdown != null) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        color = Color(0xFF006400).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Prochaine course : ${raid.countdown}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = raid.name, 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                
                raid.dates?.start?.let {
                    Text(
                        text = "Date : $it", 
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                raid.place?.let {
                    Text(
                        text = "Lieu : $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008000)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text("Voir le détail", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: VikViewModel, onLoginSuccess: () -> Unit, onNavigateToSignup: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp).padding(bottom = 32.dp)
        )
        Text("Connexion", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { 
                viewModel.login(
                    LoginRequest(username = username, password = password),
                    onLoginSuccess
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008000))
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Se connecter")
        }
        TextButton(onClick = onNavigateToSignup) {
            Text("Pas de compte ? S'inscrire", color = Color(0xFF008000))
        }
    }
}

@Composable
fun SignupScreen(viewModel: VikViewModel, onSignupSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    
    var isLicensed by remember { mutableStateOf(false) }
    var licenseNumber by remember { mutableStateOf("") }
    var selectedClubId by remember { mutableStateOf<Int?>(null) }
    var clubExpanded by remember { mutableStateOf(false) }

    val radioOptions = listOf("Oui", "Non")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inscription", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Prénom") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Date de naissance (AAAA-MM-JJ)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Téléphone") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Adresse Postal") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = zipCode, onValueChange = { zipCode = it }, label = { Text("Code Postal") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Section Licencié
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Êtes-vous licencié ?", fontWeight = FontWeight.Medium)
            Row(Modifier.selectableGroup()) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .height(48.dp)
                            .selectable(
                                selected = (if (isLicensed) "Oui" else "Non") == text,
                                onClick = { isLicensed = (text == "Oui") },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (if (isLicensed) "Oui" else "Non") == text,
                            onClick = null, // null because of selectable
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF008000))
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }

        if (isLicensed) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { clubExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = viewModel.clubs.find { it.club_id == selectedClubId }?.club_name ?: "Sélectionner un club")
                }
                DropdownMenu(expanded = clubExpanded, onDismissRequest = { clubExpanded = false }) {
                    viewModel.clubs.forEach { club ->
                        DropdownMenuItem(
                            text = { Text(club.club_name) },
                            onClick = { selectedClubId = club.club_id; clubExpanded = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licenseNumber, 
                onValueChange = { licenseNumber = it }, 
                label = { Text("Numéro de Licence (FFCO)") }, 
                placeholder = { Text("Ex: 1403958") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Pseudo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mot de passe") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = passwordConfirm, onValueChange = { passwordConfirm = it }, label = { Text("Confirmer le mot de passe") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                val request = SignupRequest(
                    mem_name = name,
                    mem_firstname = firstName,
                    mem_birthdate = birthDate,
                    mem_email = email,
                    mem_phone = phone,
                    mem_adress = address,
                    mem_zipcode = zipCode,
                    user_username = username,
                    user_password = password,
                    user_password_confirmation = passwordConfirm,
                    club_id = if (isLicensed) selectedClubId else null,
                    mem_default_licence = if (isLicensed) licenseNumber else null
                )
                viewModel.signup(request, onSignupSuccess)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008000))
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("S'inscrire")
        }
        TextButton(onClick = onNavigateToLogin) {
            Text("Déjà un compte ? Se connecter", color = Color(0xFF008000))
        }
    }
}

@Composable
fun ProfileScreen(viewModel: VikViewModel, onLogout: () -> Unit) {
    val user = viewModel.currentUser ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFFE8F5E9)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp).padding(16.dp),
                tint = Color(0xFF008000)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.user_username ?: "",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${user.mem_firstname} ${user.mem_name}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileInfoItem(label = "Email", value = user.mem_email ?: "N/A", icon = Icons.Default.Email)
                ProfileInfoItem(label = "Téléphone", value = user.mem_phone ?: "N/A", icon = Icons.Default.Phone)
                ProfileInfoItem(label = "Date de naissance", value = user.mem_birthdate ?: "N/A", icon = Icons.Default.DateRange)
                ProfileInfoItem(label = "Adresse", value = user.mem_adress ?: "N/A", icon = Icons.Default.LocationOn)
                ProfileInfoItem(label = "Licence", value = user.mem_default_licence ?: "Non licencié", icon = Icons.Default.Info)
                
                val clubName = viewModel.clubs.find { it.club_id == user.club_id }?.club_name
                ProfileInfoItem(label = "Club", value = clubName ?: "Aucun club", icon = Icons.Default.Place)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.logout(); onLogout() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Se déconnecter", color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF008000), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
