package com.example.nammasantheledger.data

import kotlinx.coroutines.flow.Flow

class SantheRepository(private val customerDao: CustomerDao, private val transactionDao: TransactionDao) {

    // Customer operations
    suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

    // Transaction operations
    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>> = transactionDao.getTransactionsForCustomer(customerId)
    fun getTodayTransactions(startOfDay: Long): Flow<List<Transaction>> = transactionDao.getTodayTransactions(startOfDay)
    fun getBalanceForCustomer(customerId: Int): Flow<Double?> = transactionDao.getBalanceForCustomer(customerId)
    fun getTotalOutstanding(): Flow<Double?> = transactionDao.getTotalOutstanding()
}