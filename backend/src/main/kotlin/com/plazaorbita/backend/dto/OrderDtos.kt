package com.plazaorbita.backend.dto

data class OrderItemRequest(val productId: Long, val quantity: Int)
data class OrderRequest(val businessId: Long, val items: List<OrderItemRequest>)
data class OrderStatusUpdateRequest(val status: String)
