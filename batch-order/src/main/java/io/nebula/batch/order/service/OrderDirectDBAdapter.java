package io.nebula.batch.order.service;

import io.nebula.batch.order.constant.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderDirectDBAdapter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int updateOrderState(OrderState desired, OrderState now, int week) {
        String sql = "UPDATE `order` SET order_state= ? " +
                "WHERE order_state = ? " +
                "AND order_date < DATE_SUB(NOW(), INTERVAL ? WEEK)";

        return jdbcTemplate.update(sql, desired.name(), now.name(), week);
    }

    public int deleteOrder(OrderState desired, int week) {
        System.out.println("deleteOrder");
        String sql = "DELETE FROM `order` " +
                "WHERE order_state = ? " +
                "AND order_date < DATE_SUB(NOW(), INTERVAL ? WEEK)";

        return jdbcTemplate.update(sql, desired.name(), week);
    }
}