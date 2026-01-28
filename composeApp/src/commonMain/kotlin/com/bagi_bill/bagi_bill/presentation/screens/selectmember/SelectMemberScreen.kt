package com.bagi_bill.bagi_bill.presentation.screens.selectmember

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme

/**
 * Select Member screen for choosing split bill participants.
 * Features:
 * - Current payer display with change option
 * - Selected members as avatar row
 * - Contact list from phone with search and checkbox
 * - Manual member addition via bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMemberScreen(
    onBack: () -> Unit,
    onConfirm: (SplitBillData) -> Unit,
    contacts: List<PhoneContact> = emptyList(),
    isLoadingContacts: Boolean = false,
    onRequestPermission: () -> Unit = {},
    hasContactPermission: Boolean = false,
    initialPayer: SelectableMember? = null,
    initialMembers: List<SelectableMember> = emptyList()
) {
    // State
    var payer by remember { 
        mutableStateOf(initialPayer ?: SelectableMember(
            name = "Saya",
            wallet = "GoPay",
            phoneNumber = "08123456789"
        )) 
    }
    var selectedMembers by remember { mutableStateOf(initialMembers) }
    var showReplacePayer by remember { mutableStateOf(false) }
    var showAddManual by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredContacts = remember(searchQuery, contacts) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter { 
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.phoneNumber.contains(searchQuery)
        }
    }
    
    val selectedContactIds = remember(selectedMembers) {
        selectedMembers.map { it.id }.toSet()
    }
    
    AnimatedContent(
        targetState = showReplacePayer,
        transitionSpec = {
            if (targetState) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        }
    ) { showPayer ->
        if (showPayer) {
            ReplacePayerContent(
                currentPayer = payer,
                // Show all members, not just canBePayer - let user see all options
                eligiblePayers = listOf(payer) + selectedMembers,
                onBack = { showReplacePayer = false },
                onConfirm = { newPayer ->
                    // Swap payer: old payer becomes member, new payer is removed from members
                    if (newPayer.id != payer.id) {
                        val oldPayer = payer
                        // Remove new payer from members (if they were a member)
                        val updatedMembers = selectedMembers.filter { it.id != newPayer.id }
                        // Add old payer to members (if not already there)
                        selectedMembers = updatedMembers + oldPayer
                        payer = newPayer
                    }
                    showReplacePayer = false
                }
            )
        } else {
            SelectMemberContent(
                payer = payer,
                selectedMembers = selectedMembers,
                contacts = filteredContacts,
                selectedContactIds = selectedContactIds,
                searchQuery = searchQuery,
                isLoadingContacts = isLoadingContacts,
                hasContactPermission = hasContactPermission,
                onSearchChange = { searchQuery = it },
                onBack = onBack,
                onChangePayer = { showReplacePayer = true },
                onAddManual = { showAddManual = true },
                onRequestPermission = onRequestPermission,
                onToggleContact = { contact ->
                    val member = SelectableMember(
                        id = contact.id,
                        name = contact.name,
                        phoneNumber = contact.phoneNumber
                    )
                    selectedMembers = if (contact.id in selectedContactIds) {
                        selectedMembers.filter { it.id != contact.id }
                    } else {
                        selectedMembers + member
                    }
                },
                onRemoveMember = { member ->
                    selectedMembers = selectedMembers.filter { it.id != member.id }
                },
                onConfirm = {
                    if (selectedMembers.isNotEmpty()) {
                        onConfirm(SplitBillData.create(payer, selectedMembers))
                    }
                }
            )
        }
    }
    
    // Bottom sheet for manual member
    if (showAddManual) {
        AddMemberBottomSheet(
            onDismiss = { showAddManual = false },
            onAdd = { member ->
                selectedMembers = selectedMembers + member
                showAddManual = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectMemberContent(
    payer: SelectableMember,
    selectedMembers: List<SelectableMember>,
    contacts: List<PhoneContact>,
    selectedContactIds: Set<String>,
    searchQuery: String,
    isLoadingContacts: Boolean,
    hasContactPermission: Boolean,
    onSearchChange: (String) -> Unit,
    onBack: () -> Unit,
    onChangePayer: () -> Unit,
    onAddManual: () -> Unit,
    onRequestPermission: () -> Unit,
    onToggleContact: (PhoneContact) -> Unit,
    onRemoveMember: (SelectableMember) -> Unit,
    onConfirm: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Pilih anggota",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.AutoMirrored.Filled.Help, "Bantuan", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // Match RincianScreen bottom bar style exactly
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = AppTheme.Elevation.bottomSticky,
                        clip = false,
                        spotColor = Color.Black,
                        ambientColor = Color.Black
                    ),
                color = Color.White,
                shape = AppTheme.Shapes.bottomSheet
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = AppTheme.Spacing.large,
                        end = AppTheme.Spacing.large,
                        top = AppTheme.Spacing.xLarge,
                        bottom = AppTheme.Spacing.xLarge
                    )
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.Size.buttonHeight),
                        enabled = selectedMembers.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                            disabledContentColor = Color.White
                        ),
                        shape = AppTheme.Shapes.pill
                    ) {
                        Text(
                            text = "Konfirmasi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Payer and Members Card
            item {
                PayerAndMembersCard(
                    payer = payer,
                    members = selectedMembers,
                    onChangePayer = onChangePayer,
                    onAddManual = onAddManual,
                    onRemoveMember = onRemoveMember
                )
            }
            
            // Contact Card (Search + List)
            item {
                ContactCard(
                    contacts = contacts,
                    selectedContactIds = selectedContactIds,
                    searchQuery = searchQuery,
                    isLoading = isLoadingContacts,
                    hasPermission = hasContactPermission,
                    onSearchChange = onSearchChange,
                    onRequestPermission = onRequestPermission,
                    onToggleContact = onToggleContact
                )
            }
        }
    }
}

@Composable
private fun PayerAndMembersCard(
    payer: SelectableMember,
    members: List<SelectableMember>,
    onChangePayer: () -> Unit,
    onAddManual: () -> Unit,
    onRemoveMember: (SelectableMember) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Payer section header
            Text(
                text = "Bayar ke",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Payer item in bordered surface (like HomeScreen options)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with icon border like HomeScreen
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = payer.initial,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = payer.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        if (payer.wallet != null) {
                            Text(
                                text = payer.wallet,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    TextButton(
                        onClick = onChangePayer,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Ganti",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray.copy(alpha = 0.1f)
            )
            
            // Members section header
            Text(
                text = "Anggota (${members.size + 1})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Pilih dari daftar kontak atau tambah manual",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Member avatars row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Payer avatar
                item {
                    MemberAvatar(
                        initial = payer.initial,
                        name = payer.name,
                        isPayer = true,
                        onRemove = null
                    )
                }
                
                // Selected members
                items(members, key = { it.id }) { member ->
                    MemberAvatar(
                        initial = member.initial,
                        name = member.name,
                        isPayer = false,
                        onRemove = { onRemoveMember(member) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Manual add button - styled like HomeScreen options
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent,
                onClick = onAddManual
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAddAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Di luar kontak",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
    contacts: List<PhoneContact>,
    selectedContactIds: Set<String>,
    searchQuery: String,
    isLoading: Boolean,
    hasPermission: Boolean,
    onSearchChange: (String) -> Unit,
    onRequestPermission: () -> Unit,
    onToggleContact: (PhoneContact) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Text(
                text = "Kontak",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Search bar in bordered surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Cari kontak...",
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Permission request or contact list
            when {
                !hasPermission -> {
                    // Permission not granted - show request button
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                        color = Color.Transparent,
                        onClick = onRequestPermission
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contacts,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Izinkan akses kontak",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap untuk mengizinkan akses ke buku kontak",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                isLoading -> {
                    // Loading contacts
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
                
                contacts.isEmpty() -> {
                    // No contacts found
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) 
                                "Tidak ada kontak" 
                            else 
                                "Tidak ditemukan \"$searchQuery\"",
                            color = Color.Gray
                        )
                    }
                }
                
                else -> {
                    // Contact list
                    Column {
                        contacts.forEachIndexed { index, contact ->
                            ContactItem(
                                contact = contact,
                                isSelected = contact.id in selectedContactIds,
                                onToggle = { onToggleContact(contact) }
                            )
                            if (index < contacts.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 56.dp),
                                    color = Color.Gray.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberAvatar(
    initial: String,
    name: String,
    isPayer: Boolean,
    onRemove: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPayer) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isPayer) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.secondary
                )
            }
            
            if (onRemove != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            if (isPayer) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallet,
                        contentDescription = "Payer",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = name.split(" ").first(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ContactItem(
    contact: PhoneContact,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with border like other icons
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.first().uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMemberBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (SelectableMember) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedWallet by remember { mutableStateOf<String?>(null) }
    var showWalletDropdown by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Tambah anggota",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name field with border
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent
            ) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor HP (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Wallet dropdown
            ExposedDropdownMenuBox(
                expanded = showWalletDropdown,
                onExpandedChange = { showWalletDropdown = it }
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                    color = Color.Transparent
                ) {
                    OutlinedTextField(
                        value = selectedWallet ?: "",
                        onValueChange = {},
                        label = { Text("Dompet (opsional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showWalletDropdown)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                }
                
                ExposedDropdownMenu(
                    expanded = showWalletDropdown,
                    onDismissRequest = { showWalletDropdown = false }
                ) {
                    indonesianWallets.forEach { wallet ->
                        DropdownMenuItem(
                            text = { Text(wallet) },
                            onClick = {
                                selectedWallet = wallet
                                showWalletDropdown = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Button consistent with other screens
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(SelectableMember(
                            name = name,
                            phoneNumber = phone.takeIf { it.isNotBlank() },
                            wallet = selectedWallet
                        ))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(AppTheme.Radius.button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Tambah",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
