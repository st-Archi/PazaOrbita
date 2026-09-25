package com.plazaorbita.app.data.model

data class RegisterRequest(val name: String, val email: String, val password: String, val role: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val token: String, val userId: Long, val name: String, val role: String)

data class Business(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val category: String, // "PRODUCT" | "SERVICE"
    val location: String?,
    val status: String
)
data class BusinessRequest(val name: String, val category: String, val location: String?, val opensAt: String?, val closesAt: String?)

data class Product(
    val id: Long,
    val businessId: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val minThreshold: Int
)
data class ProductRequest(val name: String, val description: String?, val price: Double, val stock: Int, val minThreshold: Int)

data class AppointmentRequest(val businessId: Long, val serviceName: String, val apptDate: String, val apptTime: String)
data class Appointment(
    val id: Long, val businessId: Long, val customerId: Long,
    val serviceName: String, val apptDate: String, val apptTime: String, val status: String
)

data class OrderItemRequest(val productId: Long, val quantity: Int)
data class OrderRequest(val businessId: Long, val items: List<OrderItemRequest>)
data class Order(val id: Long, val businessId: Long, val customerId: Long, val status: String)

data class Notification(val id: Long, val userId: Long, val message: String, val type: String, val isRead: Boolean)
