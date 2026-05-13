package com.example.nammasantheledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: String, // "UDARI" or "PAYMENT"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)