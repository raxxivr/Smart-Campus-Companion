package com.example.smartcampuscompanion.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.ui.theme.TealPrimary
import com.example.smartcampuscompanion.ui.theme.TealSecondary
import com.example.smartcampuscompanion.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignInClick: () -> Unit = {}, // New callback
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(TealPrimary, TealSecondary, Color(0xFF006064))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(20.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Smart Campus",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3142)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onLoginClick(username, password) })
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onLoginClick(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        enabled = username.isNotBlank() && password.isNotBlank()
                    ) {
                        Text("LOGIN", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // OR divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                        Text(
                            text = " OR ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Google Sign In Button
                    OutlinedButton(
                        onClick = onGoogleSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo), // Make sure this asset exists
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Sign in with Google",
                                color = Color(0xFF2D3142),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", color = Color.White.copy(alpha = 0.8f))
                TextButton(onClick = onSignUpClick) {
                    Text("Register Now", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
