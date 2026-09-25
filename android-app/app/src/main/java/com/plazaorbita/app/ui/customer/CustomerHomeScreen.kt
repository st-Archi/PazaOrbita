package com.plazaorbita.app.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plazaorbita.app.data.model.Business
import com.plazaorbita.app.data.remote.RetrofitClient

// Panel del cliente: explorar negocios de la plaza para reservar cita o hacer pedido
@Composable
fun CustomerHomeScreen() {
    var businesses by remember { mutableStateOf<List<Business>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.listBusinesses()
            if (response.isSuccessful) businesses = response.body().orEmpty()
        } finally {
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Negocios en Plaza Órbita", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(businesses) { b ->
                    ListItem(
                        headlineContent = { Text(b.name) },
                        supportingContent = { Text(if (b.category == "SERVICE") "Reservar cita" else "Hacer pedido (pickup)") }
                    )
                    Divider()
                }
            }
        }
        // TODO siguiente sprint: pantalla de reserva de cita (POST /api/appointments)
        // y pantalla de carrito/pedido (POST /api/orders) al tocar cada negocio.
    }
}
