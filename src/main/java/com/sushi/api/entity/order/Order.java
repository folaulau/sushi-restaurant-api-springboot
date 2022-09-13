package com.sushi.api.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import com.sushi.api.dto.ProductUuidDTO;
import com.sushi.api.entity.DatabaseTableNames;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.order.paymentmethod.OrderPaymentMethod;
import com.sushi.api.entity.payment.Payment;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.user.User;
import com.sushi.api.utils.MathUtils;
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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.ORDER + " SET deleted = 'true' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'false'")
@Table(name = DatabaseTableNames.ORDER,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Order implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER,
      mappedBy = "order")
  private Set<LineItem> lineItems;

  @JsonIgnoreProperties(value = {"orders"})
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id")
  private Address address;

  @Column(name = "delivered", nullable = false)
  private boolean delivered;

  @Column(name = "current", nullable = false)
  private boolean current;

  @Enumerated(EnumType.STRING)
  @Column(name = "delivery_method", nullable = true)
  private DeliveryMethod deliveryMethod;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @Column(name = "picked_up_at")
  private LocalDateTime pickedUpAt;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "paid", nullable = false)
  private boolean paid;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  /**
   * cost of order(all products)<br>
   * total from Order
   */
  @Column(name = "lineitems_total", nullable = true)
  private Double lineItemsTotal;

  /**
   * Service fee, $2
   */
  @Column(name = "service_fee", nullable = true)
  private Double serviceFee;

  @Column(name = "delivery_fee", nullable = true)
  private Double deliveryFee;

  /**
   * stripe fee on charge<br>
   * 2.9% of orderCost<br>
   */
  @Column(name = "stripe_fee", nullable = true)
  private Double stripeFee;


  @Column(name = "tax_fee", nullable = true)
  private Double taxFee;

  /**
   * distance to dropoff in miles
   */
  @Column(name = "drop_off_distance", nullable = true)
  private Double dropOffDistance;

  /**
   * total charge for everything = orderCost + serviceFee + stripeFee
   */
  @Column(name = "total", nullable = true)
  private Double total;


  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public double getLineItemsTotal() {
    this.lineItemsTotal = 0.0;
    if (this.lineItems != null && this.lineItems.size() > 0) {

      for (LineItem lineItem : lineItems) {
        if (lineItem != null) {
          this.lineItemsTotal += lineItem.getTotal();
        }
      }

    }
    return MathUtils.getTwoDecimalPlaces(this.lineItemsTotal);
  }

  public void setLineItemsTotal(Double lineItemsTotal) {}

  public void addLineItem(LineItem lineItem) {
    if (this.lineItems == null) {
      this.lineItems = new HashSet<>();
    }
    this.lineItems.add(lineItem);
  }

  public LineItem getLineItem(Product product) {
    LineItem lineItem = null;
    if (this.lineItems != null) {
      for (LineItem lI : this.lineItems) {
        if (product.equals(lI.getProduct())) {
          lineItem = lI;
          break;
        }
      }
    }
    return lineItem;
  }

  public LineItem getLineItem(ProductUuidDTO product) {

    if (this.lineItems == null) {
      return null;
    }

    return this.lineItems.stream().filter(pro -> {
      return pro.getProduct().getUuid().equals(product.getUuid());
    }).findFirst().orElse(null);
  }

  public Double generateTotal() {
    BigDecimal orderTotal = BigDecimal.valueOf(0.0);

    orderTotal = orderTotal.add(BigDecimal.valueOf(lineItemsTotal));

    if (serviceFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(serviceFee));
    }

    if (deliveryFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(deliveryFee));
    }

    if (stripeFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(stripeFee));
    }

    if (taxFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(taxFee));
    }

    this.total = orderTotal.doubleValue();

    return MathUtils.getTwoDecimalPlaces(this.total);
  }


  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id).append(this.uuid).toHashCode();

    // return HashCodeBuilder.reflectionHashCode(this);
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
    Order other = (Order) obj;
    return new EqualsBuilder().append(this.id, other.id).append(this.uuid, other.uuid).isEquals();
  }

  public void removeLineItem(LineItem lineItem) {
    lineItem.setDeleted(true);
    this.lineItems.remove(lineItem);
  }

  public void removeAllLineItems() {

    if (this.lineItems == null || this.lineItems.size() == 0) {
      return;
    }

    this.lineItems.stream().map(lineItem -> {
      lineItem.setDeleted(true);
      return lineItem;
    }).collect(Collectors.toSet());

    this.lineItems.removeAll(this.lineItems);

  }

  @PrePersist
  private void preCreate() {
    this.current = true;

    if (this.status == null) {
      this.status = OrderStatus.ORDERING;
    }

    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "order-" + UUID.randomUUID().toString();
    }

    if (serviceFee == null) {
      serviceFee = 0.0;
    }

    if (stripeFee == null) {
      stripeFee = 0.0;
    }

    if (taxFee == null) {
      taxFee = 0.0;
    }

    if (dropOffDistance == null) {
      dropOffDistance = 0.0;
    }


    if (deliveryFee == null) {
      deliveryFee = 0.0;
    }


    generateTotal();
  }

  @PreUpdate
  private void preUpdate() {
    generateTotal();
  }
}
