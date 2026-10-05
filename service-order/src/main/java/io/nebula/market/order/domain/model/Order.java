package io.nebula.market.order.domain.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@DynamicUpdate
@DynamicInsert
@Getter
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "`order`")
@Comment("주문")
public class Order implements Serializable {
    @Id
    @Tsid
    @Comment("ID")
    private Long id;

    @Comment("상품 ID")
    private Long productId;

    @Comment("계정 ID")
    private UUID accountId;

    @Comment("가격")
    @ColumnDefault("0")
    private Long price;

    @Comment("수량")
    @ColumnDefault("1")
    private Integer quantity;

    @CreatedDate
    @Column(updatable = false, nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Comment("주문시각")
    private OffsetDateTime orderDate;

    @Comment("주문 상태")
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PENDING'")
    private OrderState orderState;

    public Order changeOrderState(OrderState state) {
        this.orderState = state;
        return this;
    }
}