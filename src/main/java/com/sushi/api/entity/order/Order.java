package com.sushi.api.entity.order;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
import com.sushi.api.entity.payment.Payment;
import com.sushi.api.entity.product.Product;
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

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "paid", nullable = false)
  private boolean paid;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "total")
  private Double total;

  public double getTotal() {
    this.total = 0.0;
    if (this.lineItems != null && this.lineItems.size() > 0) {
      this.lineItems.forEach((lineItem) -> {
        if (lineItem != null) {
          this.total += lineItem.getTotal();
        }
      });
      this.total = MathUtils.getTwoDecimalPlaces(this.total);
    }
    return this.total;
  }


  public void stampPayment(Payment payment) {
    this.payment = payment;
    this.paid = payment.getPaid();
    this.paidAt = LocalDateTime.now();
  }

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

  @PrePersist
  private void preCreate() {
    this.current = true;

    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "order-" + UUID.randomUUID().toString();
    }
  }

  @PreUpdate
  private void preUpdate() {}
}
