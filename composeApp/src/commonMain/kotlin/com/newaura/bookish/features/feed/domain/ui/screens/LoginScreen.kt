package com.newaura.bookish.features.feed.domain.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx. compose.ui.unit.dp
import com.newaura.bookish.features.feed.domain.ui.LoginUiState
import com.newaura.bookish.features.feed.domain.ui.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String) -> Unit
) {
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(screenState.uiState) {
        if (screenState.uiState is LoginUiState.Success) {
            onLoginSuccess((screenState.uiState as LoginUiState.Success).userId)
        }
    }

    if (screenState.isOtpScreen) {
        OtpVerificationContent(
            otp = screenState.otp,
            phoneNumber = screenState.phoneNumber,
            isLoading = screenState.uiState is LoginUiState.Loading,
            error = (screenState.uiState as?  LoginUiState.Error)?.message,
            onOtpChange = viewModel::updateOtp,
            onVerify = viewModel::verifyOtp,
            onBack = viewModel::goBack
        )
    } else {
        PhoneLoginContent(
            phoneNumber = screenState.phoneNumber,
            isLoading = screenState.uiState is LoginUiState.Loading,
            error = (screenState.uiState as? LoginUiState.Error)?.message,
            onPhoneChange = viewModel::updatePhoneNumber,
            onSendOtp = viewModel::sendOtp,
            onGoogleSignIn = viewModel::signInWithGoogle
        )
    }
}

@Composable
private fun PhoneLoginContent(
    phoneNumber: String,
    isLoading: Boolean,
    error: String?,
    onPhoneChange:  (String) -> Unit,
    onSendOtp: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Bookish",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        error?.let {
            Spacer(modifier = Modifier. height(8.dp))
            Text(
                text = it,
                color = MaterialTheme. colorScheme.error,
                style = MaterialTheme. typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSendOtp,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && phoneNumber.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Send OTP")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Sign in with Google")
        }
    }
}

@Composable
private fun OtpVerificationContent(
    otp: String,
    phoneNumber: String,
    isLoading: Boolean,
    error: String?,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement. Center
    ) {
        Text(
            text = "Verification Code",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier. height(8.dp))

        Text(
            text = "Enter the OTP sent to $phoneNumber",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme. colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier. height(32.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) onOtpChange(it) },
            label = { Text("OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme. typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerify,
            modifier = Modifier.fillMaxWidth(),
            enabled = ! isLoading && otp.length >= 5
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme. onPrimary
                )
            } else {
                Text("Verify")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Go Back")
        }
    }
}