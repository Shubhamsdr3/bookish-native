package com.newaura.bookish.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.util.customImePadding
import com.newaura.bookish.features.feed.ui.LoginUiState
import com.newaura.bookish.features.feed.ui.LoginViewModel
import com.newaura.bookish.features.feed.ui.screens.HomeFeedScreen
import com.newaura.bookish.features.home.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

class OtpVerificationScreen(val phoneNumber: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: LoginViewModel = koinViewModel<LoginViewModel>()

        val screenState by viewModel.screenState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(screenState.uiState) {
            if (screenState.uiState is LoginUiState.Success) {
                navigator.push(HomeScreen())
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OTP Verification") },
                    navigationIcon = {
                        TextButton(onClick = {
                            navigator.pop()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .customImePadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Enter the OTP sent to $phoneNumber",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(
                    value = screenState.otp,
                    onValueChange = { if (it.length <= 6) viewModel.onOtpChange(it) },
                    label = { Text("OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::verifyOtp,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = screenState.otp.length == 6
                ) {
                    val isLoading = screenState.uiState == LoginUiState.Loading
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Verify")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    navigator.pop()
                }) {
                    Text("Go Back")
                }
            }
        }
    }
}