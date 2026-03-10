package com.example.examen_final

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Importante: Estos imports pueden salir en rojo si no hay conexión con Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- MODELOS DE DATOS ---

// Ejercicio 42: Data class Product
data class Product(
    val id: Int,
    val name: String,
    val price: Double
)

// Ejercicio 44: Data class Ride
data class Ride(
    val firestoreId: String = "",
    val userId: String = "",
    val destination: String = ""
)

// --- VIEWMODEL ---

class ProductViewModel : ViewModel() {

    // LOGICA EJERCICIO 42
    // Lista observable con inicialización (1 pt)
    var productList by mutableStateOf(
        listOf(
            Product(1, "Laptop", 999.99),
            Product(2, "Mouse", 25.50)
        )
    )

    private var nextId = 3

    // Función añadir con ID autoincrementado (1 pt)
    fun addProduct(name: String, price: Double) {
        val newProduct = Product(id = nextId, name = name, price = price)
        productList = productList + newProduct
        nextId++
    }

    // Función eliminar con filter (0.5 pts)
    fun deleteProduct(id: Int) {
        productList = productList.filter { it.id != id }
    }

    // Función buscar con find (0.5 pts)
    fun getProductById(id: Int): Product? {
        return productList.find { it.id == id }
    }

    // LOGICA EJERCICIO 44 (FIREBASE)

    // Estado para guardar el historial (0.5 pts)
    var rideHistory by mutableStateOf<List<Ride>>(listOf())

    fun loadMyRides() {
        // 1. Comprobar usuario logueado (0.5 pts)
        val user = Firebase.auth.currentUser ?: return
        val db = Firebase.firestore

        // 6. Ejecutar en viewModelScope (0.5 pts)
        viewModelScope.launch {
            try {
                // 2. Leer de Firestore con filtro y await (1 pt)
                val snapshot = db.collection("rides")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()

                // 3. Mapear documentos y usar copy para el ID (1 pt)
                val rides = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Ride::class.java)?.copy(firestoreId = doc.id)
                }

                // 4. Asignar resultado (0.5 pts)
                rideHistory = rides

            } catch (e: Exception) {
                // 5. Fallback en caso de error (0.5 pts)
                rideHistory = listOf()
            }
        }
    }
}