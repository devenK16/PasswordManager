package com.example.passwordmanager.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.passwordmanager.data.Password
import com.example.passwordmanager.ui.viewmodel.PasswordViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordManagerTheme {
                setBarColor(MaterialTheme.colorScheme.inverseOnSurface)
                PasswordManagerApp()
            }
        }
    }
    @Composable
    private fun setBarColor( color : Color){
        val systemUiController = rememberSystemUiController()
        LaunchedEffect(key1 = color) {
            systemUiController.setSystemBarsColor(color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordManagerApp(viewModel: PasswordViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf<Password?>(null) }
    val bottomSheetState = rememberModalBottomSheetState()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = bottomSheetState
        ) {
            AddEditPasswordScreen(
                viewModel = viewModel,
                isEditing = isEditing,
                password = currentPassword,
                onClose = {
                    coroutineScope.launch { bottomSheetState.hide() }
                    showSheet = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Password Manager") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isEditing = false
                currentPassword = null
                coroutineScope.launch {
                    showSheet = true
                    bottomSheetState.show()
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Password")
            }
        }
    ) { innerPadding ->
        PasswordListScreen(
            viewModel = viewModel,
            onPasswordClick = { password ->
                isEditing = true
                currentPassword = password
                coroutineScope.launch {
                    showSheet = true
                    bottomSheetState.show()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun PasswordListScreen(viewModel: PasswordViewModel, onPasswordClick: (Password) -> Unit, modifier: Modifier = Modifier) {
    val passwords by viewModel.passwords.collectAsState()

    LazyColumn(modifier = modifier) {
        items(passwords) { password ->
            PasswordRow(password, onPasswordClick)
        }
    }
}

@Composable
fun PasswordRow(password: Password, onPasswordClick: (Password) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onPasswordClick(password) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(text = password.accountType, fontSize = 18.sp , modifier = Modifier.padding(end = 8.dp))
                Text(text = "*****", fontSize = 18.sp)
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Composable
fun AddEditPasswordScreen(
    viewModel: PasswordViewModel,
    isEditing: Boolean,
    password: Password? = null,
    onClose: () -> Unit
) {
    var accountType by remember { mutableStateOf(password?.accountType ?: "") }
    var username by remember { mutableStateOf(password?.username ?: "") }
    var passwordText by remember { mutableStateOf(password?.password ?: "") }

    Column(modifier = Modifier.padding(start = 16.dp , top = 16.dp , end = 16.dp , bottom = 30.dp)) {
        OutlinedTextField(
            value = accountType,
            onValueChange = { accountType = it },
            label = { Text("Account Type") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                if (isEditing && password != null) {
                    viewModel.updatePassword(password.id, accountType, username, passwordText)
                } else {
                    viewModel.addPassword(accountType, username, passwordText)
                }
                onClose()
            }) {
                Text(if (isEditing) "Update" else "Add")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isEditing) {
                Button(onClick = {
                    if (password != null) {
                        viewModel.deletePassword(password.id)
                    }
                    onClose()
                }) {
                    Text("Delete")
                }
            }
        }
    }
}
