package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Add custom queries if needed

    // Find by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find orders by user, sorted by order date desc
    List<Order> findByUserOrderByOrderDateDesc(com.stefan.ecommerce.entities.User user);

    // Find orders by user ID, sorted by order date desc
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // Find orders by status, sorted by order date desc
    List<Order> findByStatusOrderByOrderDateDesc(com.stefan.ecommerce.entities.Order.OrderStatus status);

    // Find orders by order date range, sorted by order date desc
    List<Order> findByOrderDateBetweenOrderByOrderDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by total amount range, sorted by order date desc
    List<Order> findByTotalAmountBetweenOrderByOrderDateDesc(BigDecimal minAmount, BigDecimal maxAmount);

    // Exists by order number
    boolean existsByOrderNumber(String orderNumber);

    // Count by user
    long countByUser(com.stefan.ecommerce.entities.User user);

    // Count by status
    long countByStatus(com.stefan.ecommerce.entities.Order.OrderStatus status);

    // Find all orders sorted by order date desc
    List<Order> findAllByOrderByOrderDateDesc();

    // Find all orders sorted by order date asc
    List<Order> findAllByOrderByOrderDateAsc();

    // Find all orders sorted by total amount desc
    List<Order> findAllByOrderByTotalAmountDesc();

    // Find all orders sorted by total amount asc
    List<Order> findAllByOrderByTotalAmountAsc();

    // Find orders by user and status, sorted by order date desc
    List<Order> findByUserAndStatusOrderByOrderDateDesc(com.stefan.ecommerce.entities.User user, com.stefan.ecommerce.entities.Order.OrderStatus status);

    // Find orders by user and order date range, sorted by order date desc
    List<Order> findByUserAndOrderDateBetweenOrderByOrderDateDesc(com.stefan.ecommerce.entities.User user, LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by user and total amount range, sorted by order date desc
    List<Order> findByUserAndTotalAmountBetweenOrderByOrderDateDesc(com.stefan.ecommerce.entities.User user, BigDecimal minAmount, BigDecimal maxAmount);

    // Revenue and statistics queries (use @Query for aggregation)
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    BigDecimal findSumOfTotalAmountByStatus(@Param("status") com.stefan.ecommerce.entities.Order.OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal findSumOfTotalAmountByStatusAndOrderDateBetween(@Param("status") com.stefan.ecommerce.entities.Order.OrderStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user AND o.status = :status")
    BigDecimal findSumOfTotalAmountByUserAndStatus(@Param("user") com.stefan.ecommerce.entities.User user, @Param("status") com.stefan.ecommerce.entities.Order.OrderStatus status);

    @Query("SELECT COALESCE(AVG(o.totalAmount),0) FROM Order o WHERE o.status = :status")
    int findAverageOrderValueByStatus(@Param("status") com.stefan.ecommerce.entities.Order.OrderStatus status);

    @Query("SELECT COALESCE(AVG(o.totalAmount),0) FROM Order o WHERE o.user = :user AND o.status = :status")
    int findAverageOrderValueByUserAndStatus(@Param("user") com.stefan.ecommerce.entities.User user, @Param("status") com.stefan.ecommerce.entities.Order.OrderStatus status);

    // Find recent orders (limit)
    @Query(value = "SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findTopByOrderByOrderDateDesc(org.springframework.data.domain.Pageable pageable);

    @Query(value = "SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findTopByUserOrderByOrderDateDesc(@Param("user") com.stefan.ecommerce.entities.User user, org.springframework.data.domain.Pageable pageable);

    // Find high value orders
    List<Order> findByTotalAmountGreaterThanOrderByOrderDateDesc(BigDecimal threshold);

    // Find low value orders
    List<Order> findByTotalAmountLessThanOrderByOrderDateDesc(BigDecimal threshold);
} 