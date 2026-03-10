package com.example.examen_final

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class Product(
    val id: Int,
    val name: String,
    val price: Double
)


data class Ride(
    val firestoreId: String = "",
    val userId: String = "",
    val destination: String = ""
)



class ProductViewModel : ViewModel() {

    var productList by mutableStateOf(
        listOf(
            Product(1, "Laptop", 999.99),
            Product(2, "Mouse", 25.50)
        )
    )

    private var nextId = 3

    fun addProduct(name: String, price: Double) {
        val newProduct = Product(id = nextId, name = name, price = price)
        productList = productList + newProduct
        nextId++
    }

    fun deleteProduct(id: Int) {
        productList = productList.filter { it.id != id }
    }

    fun getProductById(id: Int): Product? {
        return productList.find { it.id == id }
    }


    var rideHistory by mutableStateOf<List<Ride>>(listOf())

    fun loadMyRides() {
        val user = Firebase.auth.currentUser ?: return
        val db = Firebase.firestore

        viewModelScope.launch {
            try {
                // 2. Leer de Firestore con filtro y await (1 pt)
                val snapshot = db.collection("rides")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()

                val rides = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Ride::class.java)?.copy(firestoreId = doc.id)
                }

                rideHistory = rides

            } catch (e: Exception) {
                rideHistory = listOf()
            }
        }
    }
}