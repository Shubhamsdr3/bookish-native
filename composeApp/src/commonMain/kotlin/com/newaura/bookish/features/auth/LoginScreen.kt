package com.newaura.bookish.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.getActivityContext
import com.newaura.bookish.features.feed.ui.ButtonState
import com.newaura.bookish.features.feed.ui.LoginUiState
import com.newaura.bookish.features.feed.ui.LoginViewModel
import com.newaura.bookish.features.feed.ui.screens.HomeFeedScreen
import org.koin.compose.viewmodel.koinViewModel


class LoginScreen(
//    onLoginSuccess: (String) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: LoginViewModel = koinViewModel<LoginViewModel>()

        val screenState by viewModel.screenState.collectAsState()

        val activityContext = getActivityContext()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(screenState.uiState) {
            if (screenState.uiState is LoginUiState.Success) {
                navigator.push(HomeFeedScreen())
            }
        }

        if (screenState.isOtpScreen) {
            OtpVerificationContent(
                otp = screenState.otp,
                phoneNumber = screenState.phoneNumber,
                isLoading = screenState.uiState is LoginUiState.Loading,
                error = (screenState.uiState as? LoginUiState.Error)?.message,
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
                onSendOtp =  {
                    viewModel.sendOtp(activityContext)
                },
                enabled = viewModel.otpButtonState.value == ButtonState.Enabled,
                onGoogleSignIn = viewModel::signInWithGoogle
            )
        }
    }

    @Composable
    private fun PhoneLoginContent(
        phoneNumber: String,
        isLoading: Boolean,
        error: String?,
        enabled: Boolean,
        onPhoneChange: (String) -> Unit,
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
                text = "Bookish",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    if (newValue.length <= 10) {
                        onPhoneChange(newValue)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                label = {
                    TextViewBody(
                        "Enter 10-digit mobile number",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                leadingIcon = {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🇮🇳", fontSize = 14.sp)
                        Text(text = "+91")
                    }
                }
            )

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSendOtp,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && enabled
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Verification Code",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter the OTP sent to $phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVerify,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && otp.length >= 5
            ) {
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

            TextButton(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}