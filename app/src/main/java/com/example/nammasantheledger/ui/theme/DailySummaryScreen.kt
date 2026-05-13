package com.example.nammasantheledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.viewmodel.SantheViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryScreen(navController: NavController, viewModel: SantheViewModel) {
    val todayTransactions by viewModel.todayTransactions.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val scope = rememberCoroutineScope()

    var aiInsight by remember { mutableStateOf("Tap below to get AI insight...") }
    var isLoading by remember { mutableStateOf(false) }

    val todaySales = todayTransactions.filter { it.type == "UDARI" }.sumOf { it.amount }
    val todayCollected = todayTransactions.filter { it.type == "PAYMENT" }.sumOf { it.amount }
    val todayDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Daily Summary", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📅 $todayDate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💰", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹${todaySales.toInt()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            "Today's Udari",
                            color = Color(0xFFA5D6A7),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✅", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹${todayCollected.toInt()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            "Collected",
                            color = Color(0xFFA5D6A7),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Total Outstanding",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Text(
                            "₹${totalOutstanding.toInt()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI Insight",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                            color = Color(0xFF1B5E20)
                        )
                    } else {
                        Text(
                            text = aiInsight,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val result = withContext(Dispatchers.IO) {
                                        val apiKey = "AIzaSyAKkc_ylWbA8Tm15B7Rr75E8AtSFguPDLw"
                                        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                                        val connection = url.openConnection() as HttpsURLConnection
                                        connection.requestMethod = "POST"
                                        connection.setRequestProperty("Content-Type", "application/json")
                                        connection.doOutput = true
                                        connection.connectTimeout = 15000
                                        connection.readTimeout = 15000

                                        val body = "{\"contents\":[{\"parts\":[{\"text\":\"You are a financial assistant for a small rural market vendor in India. Today Udari given: Rs ${todaySales.toInt()}, Collected: Rs ${todayCollected.toInt()}, Total Outstanding: Rs ${totalOutstanding.toInt()}. Give 2-3 lines of simple friendly advice in English. Be warm and encouraging.\"}]}]}"
                                        connection.outputStream.write(body.toByteArray())

                                        val responseCode = connection.responseCode
                                        if (responseCode == 200) {
                                            connection.inputStream.bufferedReader().readText()
                                        } else {
                                            "ERROR ${responseCode}: " + connection.errorStream.bufferedReader().readText()
                                        }
                                    }
                                    // Show raw response first
                                    val json = org.json.JSONObject(result)
                                    aiInsight = json.getJSONArray("candidates")
                                        .getJSONObject(0)
                                        .getJSONObject("content")
                                        .getJSONArray("parts")
                                        .getJSONObject(0)
                                        .getString("text")
                                } catch (e: Exception) {
                                    aiInsight = "Error type: ${e.javaClass.simpleName} - ${e.message}"
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Text("✨ Get AI Insight", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}