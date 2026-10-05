package io.nebula.market.product.domain.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@DynamicUpdate
@DynamicInsert
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseTimeEntity implements Serializable {
    @Id
    @Tsid
    @Comment("ID")
    private Long id;

    @Comment("상품명")
    private String name;

    @Comment("이미지")
    private String image;

    @Comment("설명")
    private String description;

    @ColumnDefault("0")
    @Comment("가격")
    private Long price;

    @ColumnDefault("1")
    @Comment("재고")
    private Integer stock;

    public Product update(Product product) {
        this.name = product.name != null ? product.name : this.name;
        this.image = product.image != null ? product.image : this.image;
        this.description = product.description != null ? product.description : this.description;
        this.price = product.price;
        this.stock = product.stock;
        return this;
    }

    public Product decreaseStock(int quantity) {
        this.stock -= quantity;
        return this;
    }
}
