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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.viewmodel.SantheViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: SantheViewModel,
    customerId: Int,
    customerName: String
) {
    var amount by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("UDARI") }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Transaction",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20)
                )
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
            // Customer Name Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👤", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = customerName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Transaction Type Toggle
            Text(
                text = "Transaction Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { transactionType = "UDARI" },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "UDARI")
                            Color(0xFFD32F2F) else Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(
                        "💸 Udari",
                        color = if (transactionType == "UDARI") Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { transactionType = "PAYMENT" },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "PAYMENT")
                            Color(0xFF1B5E20) else Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(
                        "✅ Payment",
                        color = if (transactionType == "PAYMENT") Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Amount Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (amount.isEmpty()) "₹0" else "₹$amount",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (transactionType == "UDARI")
                            Color(0xFFD32F2F) else Color(0xFF1B5E20)
                    )
                    if (amountError) {
                        Text("Please enter an amount", color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            // Large Numeric Keypad
            val keys = listOf("1","2","3","4","5","6","7","8","9","⌫","0","✓")
            val keyColors = mapOf(
                "⌫" to Color(0xFFFF5722),
                "✓" to Color(0xFF1B5E20)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    keys.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                Button(
                                    onClick = {
                                        when (key) {
                                            "⌫" -> if (amount.isNotEmpty())
                                                amount = amount.dropLast(1)
                                            "✓" -> {
                                                if (amount.isEmpty()) {
                                                    amountError = true
                                                } else {
                                                    viewModel.addTransaction(
                                                        customerId = customerId,
                                                        amount = amount.toDouble(),
                                                        type = transactionType,
                                                        note = note
                                                    )
                                                    navController.popBackStack()
                                                }
                                            }
                                            else -> {
                                                amountError = false
                                                if (amount.length < 6) amount += key
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = keyColors[key] ?: Color(0xFFF5F5F5)
                                    )
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (keyColors.containsKey(key))
                                            Color.White else Color.Black
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
