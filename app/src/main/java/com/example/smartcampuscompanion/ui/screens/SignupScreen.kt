package com.example.smartcampuscompanion.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartcampuscompanion.ui.components.ErrorDialog
import com.example.smartcampuscompanion.ui.components.StyledButton
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.ui.theme.TealSecondary
import com.example.smartcampuscompanion.viewmodel.SignupViewModel

@Composable
fun SignupScreen(
    onSignupClick: (String, String, String, String, String) -> Unit,
    onBackToLoginClick: () -> Unit,
    viewModel: SignupViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(TealPrimary, TealSecondary)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join Smart Campus",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.95f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SignupTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = "Full Name",
                        icon = Icons.Default.Person,
                        focusManager = focusManager
                    )

                    SignupTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email Address",
                        icon = Icons.Default.Email,
                        focusManager = focusManager,
                        keyboardType = KeyboardType.Email
                    )

                    SignupTextField(
                        value = uiState.studentNumber,
                        onValueChange = viewModel::onStudentNumberChange,
                        label = "Student Number",
                        icon = Icons.Default.Badge,
                        focusManager = focusManager,
                        keyboardType = KeyboardType.Number
                    )

                    SignupTextField(
                        value = uiState.course,
                        onValueChange = viewModel::onCourseChange,
                        label = "Course",
                        icon = Icons.Default.School,
                        focusManager = focusManager
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary) },
                        trailingIcon = {
                            IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                            }
                        },
                        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary) },
                        trailingIcon = {
                            IconButton(onClick = viewModel::onToggleConfirmPasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                            }
                        },
                        visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StyledButton(
                        text = "Sign Up",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.signup()
                        },
                        enabled = !uiState.isLoading
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Already have an account?", color = Color.Gray)
                        TextButton(onClick = onBackToLoginClick) {
                            Text(text = "Login", color = TealPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (uiState.errorMessage != null) {
        ErrorDialog(
            message = uiState.errorMessage!!,
            onDismiss = { viewModel.clearError() }
        )
    }
}

@Composable
fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    focusManager: FocusManager,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TealPrimary) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
    )
}
