package com.plazaorbita.backend.service

import com.plazaorbita.backend.dto.OrderRequest
import com.plazaorbita.backend.model.*
import com.plazaorbita.backend.repository.NotificationRepository
import com.plazaorbita.backend.repository.OrderItemRepository
import com.plazaorbita.backend.repository.OrderRepository
import com.plazaorbita.backend.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepo: OrderRepository,
    private val itemRepo: OrderItemRepository,
    private val productRepo: ProductRepository,
    private val notificationRepo: NotificationRepository
) {
    fun listByBusiness(businessId: Long): List<Order> = orderRepo.findByBusinessId(businessId)
    fun listByCustomer(customerId: Long): List<Order> = orderRepo.findByCustomerId(customerId)

    // Historia 3: pedido con pickup, sin pago en línea
    fun createOrder(customerId: Long, req: OrderRequest): Order {
        val order = orderRepo.save(Order(businessId = req.businessId, customerId = customerId))

        var ownerIdForNotification: Long? = null
        req.items.forEach { item ->
            val product = productRepo.findById(item.productId)
                .orElseThrow { NoSuchElementException("Producto ${item.productId} no encontrado") }

            if (product.stock < item.quantity) {
                throw IllegalStateException("Stock insuficiente para ${product.name}")
            }
            product.stock -= item.quantity
            productRepo.save(product)

            itemRepo.save(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id!!,
                    quantity = item.quantity,
                    unitPrice = product.price
                )
            )
        }

        return order
    }

    // Historia 4: notificar cambios de estatus (pendiente -> listo -> entregado)
    fun updateStatus(id: Long, status: OrderStatus): Order {
        val order = orderRepo.findById(id).orElseThrow { NoSuchElementException("Pedido no encontrado") }
        order.status = status
        val saved = orderRepo.save(order)

        if (status == OrderStatus.READY_FOR_PICKUP) {
            notificationRepo.save(
                Notification(
                    userId = order.customerId,
                    message = "Tu pedido #${order.id} ya está listo para recoger",
                    type = NotificationType.ORDER
                )
            )
        }
        return saved
    }
}
