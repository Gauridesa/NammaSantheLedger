package com.example.nammasantheledger.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getTodayTransactions(startOfDay: Long): Flow<List<Transaction>>

    @Query("SELECT SUM(CASE WHEN type = 'UDARI' THEN amount ELSE -amount END) FROM transactions WHERE customerId = :customerId")
    fun getBalanceForCustomer(customerId: Int): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'UDARI' THEN amount ELSE -amount END) FROM transactions")
    fun getTotalOutstanding(): Flow<Double?>
}