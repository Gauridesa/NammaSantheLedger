package com.example.nammasantheledger.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammasantheledger.data.AppDatabase
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.data.SantheRepository
import com.example.nammasantheledger.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class SantheViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SantheRepository

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _totalOutstanding = MutableStateFlow(0.0)
    val totalOutstanding: StateFlow<Double> = _totalOutstanding.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _todayTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val todayTransactions: StateFlow<List<Transaction>> = _todayTransactions.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SantheRepository(db.customerDao(), db.transactionDao())
        loadCustomers()
        loadTotalOutstanding()
        loadTodayTransactions()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            repository.getAllCustomers().collect {
                _customers.value = it
            }
        }
    }

    private fun loadTotalOutstanding() {
        viewModelScope.launch {
            repository.getTotalOutstanding().collect {
                _totalOutstanding.value = it ?: 0.0
            }
        }
    }

    private fun loadTodayTransactions() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis
        viewModelScope.launch {
            repository.getTodayTransactions(startOfDay).collect {
                _todayTransactions.value = it
            }
        }
    }

    fun searchCustomers(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.searchCustomers(query).collect {
                _customers.value = it
            }
        }
    }

    fun addCustomer(name: String, phone: String) {
        viewModelScope.launch {
            repository.insertCustomer(Customer(name = name, phone = phone))
        }
    }

    fun addTransaction(customerId: Int, amount: Double, type: String, note: String = "") {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    type = type,
                    note = note
                )
            )
        }
    }

    fun getBalanceForCustomer(customerId: Int) = repository.getBalanceForCustomer(customerId)
    fun getTransactionsForCustomer(customerId: Int) = repository.getTransactionsForCustomer(customerId)
}