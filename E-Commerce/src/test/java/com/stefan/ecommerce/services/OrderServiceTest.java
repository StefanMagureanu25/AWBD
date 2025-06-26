package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.Order;
import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        List<Order> orders = orderService.getAllOrders();
        assertEquals(2, orders.size());
    }

    @Test
    void testFindById() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Optional<Order> found = orderService.getOrderById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void testSave() {
        Order order = new Order();
        when(orderRepository.save(order)).thenReturn(order);
        Order saved = orderService.saveOrder(order);
        assertNotNull(saved);
    }

    @Test
    void testDeleteById() {
        orderService.deleteOrder(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }
} 