package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;

public class CreditHistory extends BaseEntity<CreditHistoryId> {

  private final CustomerId customerId;
  private final TransactionType transactionType;
  private final Money amount;

  private CreditHistory(Builder builder) {
    setId(builder.creditHistoryId);
    customerId = builder.customerId;
    transactionType = builder.transactionType;
    amount = builder.amount;
  }

  public static Builder builder() {
    return new Builder();
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public Money getAmount() {
    return amount;
  }

  public static final class Builder {
    private CreditHistoryId creditHistoryId;
    private CustomerId customerId;
    private TransactionType transactionType;
    private Money amount;

    private Builder() {}

    public Builder creditHistoryId(CreditHistoryId val) {
      creditHistoryId = val;
      return this;
    }

    public Builder customerId(CustomerId val) {
      customerId = val;
      return this;
    }

    public Builder transactionType(TransactionType val) {
      transactionType = val;
      return this;
    }

    public Builder amount(Money val) {
      amount = val;
      return this;
    }

    public CreditHistory build() {
      return new CreditHistory(this);
    }
  }
}
