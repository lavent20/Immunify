package com.example.immunify.ui.presentation.login_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    navController: NavController,
    viewModelUser: UserViewModel,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.signInState.collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.blue1)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo_immunify), contentDescription = "logo", modifier = Modifier.size(200.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(32.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Login", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(36.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        scope.launch { viewModel.loginUser(email, password) }
                        
                        // AMBIL USERNAME DARI DATABASE BERDASARKAN EMAIL
                        usersRef.get().addOnSuccessListener { snapshot ->
                            snapshot.children.forEach { user ->
                                val dbEmail = user.child("email").value?.toString() ?: ""
                                if (dbEmail.equals(email, ignoreCase = true)) {
                                    val foundUsername = user.child("username").value?.toString() 
                                        ?: user.key ?: "Pengguna"
                                    viewModelUser.setUsername(foundUsername)
                                    Log.d("Login", "SESSION ESTABLISHED for: $foundUsername")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.blue1))
                ) { Text(text = "Login") }
                
                if (state.value?.isLoading == true) {
                    CircularProgressIndicator(modifier = Modifier.padding(12.dp), color = colorResource(id = R.color.blue1))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Belum punya akun?")
                    TextButton(onClick = { navController.navigate(Route.SIGNUP) }) { Text(text = "Sign Up", color = colorResource(id = R.color.blue1)) }
                }
            }
        }
    }

    LaunchedEffect(state.value?.isSuccess) {
        if (state.value?.isSuccess?.isNotEmpty() == true) {
            Toast.makeText(context, "Berhasil login!", Toast.LENGTH_SHORT).show()
            navController.navigate(Route.HOME) { popUpTo(Route.LOGIN) { inclusive = true } }
        }
    }
}
