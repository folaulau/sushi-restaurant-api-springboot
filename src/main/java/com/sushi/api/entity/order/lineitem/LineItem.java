package com.sushi.api.entity.order.lineitem;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.DatabaseTableNames;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.product.ProductType;
import com.sushi.api.utils.MathUtils;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.LINE_ITEM + " SET deleted = true WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = false")
@Table(name = DatabaseTableNames.LINE_ITEM,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class LineItem implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
  @JoinColumn(name = "product_id", nullable = false, unique = false)
  private Product product;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, updatable = false)
  private Order order;

  @Column(name = "count")
  private Integer count;

  @Column(name = "total")
  private Double total;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;



  public double getTotal() {
    if (this.product != null) {
      this.total = this.product.getPrice() * this.count;
    }
    return total;
  }


  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.product).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    LineItem other = (LineItem) obj;
    return new EqualsBuilder().append(this.product, other.product).isEquals();
  }

  public void calculateTotal() {
    if (this.product != null) {
      this.total = MathUtils.getTwoDecimalPlaces(this.product.getPrice() * this.count);
    }
  }

  @PrePersist
  private void preCreate() {

    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "line-item-" + UUID.randomUUID().toString();
    }

    calculateTotal();
  }

  @PreUpdate
  private void preUpdate() {
    calculateTotal();
  }

}
