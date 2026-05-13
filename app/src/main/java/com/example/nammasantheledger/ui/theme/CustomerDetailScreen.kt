package com.example.nammasantheledger.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.viewmodel.SantheViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    navController: NavController,
    viewModel: SantheViewModel,
    customerId: Int,
    customerName: String,
    customerPhone: String
) {
    val transactions by viewModel.getTransactionsForCustomer(customerId)
        .collectAsState(initial = emptyList())
    val balance by viewModel.getBalanceForCustomer(customerId)
        .collectAsState(initial = 0.0)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        customerName,
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_transaction/$customerId/$customerName")
                },
                containerColor = Color(0xFF1B5E20)
            ) {
                Text("₹+", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Balance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if ((balance ?: 0.0) > 0)
                        Color(0xFFD32F2F) else Color(0xFF1B5E20)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if ((balance ?: 0.0) > 0) "Amount Due" else "No Dues",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${String.format("%.0f", balance ?: 0.0)}",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📞 $customerPhone",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }

            // WhatsApp Reminder Button
            if ((balance ?: 0.0) > 0 && customerPhone.isNotEmpty()) {
                Button(
                    onClick = {
                        val message = "Namaste $customerName! " +
                                "Your pending balance is ₹${
                                    String.format("%.0f", balance ?: 0.0)
                                } at Namma Santhe. " +
                                "ನಮಸ್ಕಾರ, ನಿಮ್ಮ ₹${
                                    String.format("%.0f", balance ?: 0.0)
                                } ಬಾಕಿ ಇದೆ. ದಯವಿಟ್ಟು ಪಾವತಿಸಿ. ಧನ್ಯವಾದ!"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(
                                "https://wa.me/91$customerPhone?text=${
                                    Uri.encode(message)
                                }"
                            )
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366)
                    )
                ) {
                    Text(
                        "📱 Send WhatsApp Reminder",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Transaction History
            Text(
                text = "Transaction History",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color(0xFF1B5E20)
            )

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No transactions yet!",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (transaction.type == "UDARI") "💸" else "✅",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (transaction.type == "UDARI")
                                            "Udari Given" else "Payment Received",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = SimpleDateFormat(
                                            "dd MMM yyyy, hh:mm a",
                                            Locale.getDefault()
                                        ).format(Date(transaction.timestamp)),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "₹${String.format("%.0f", transaction.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (transaction.type == "UDARI")
                                        Color(0xFFD32F2F) else Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

