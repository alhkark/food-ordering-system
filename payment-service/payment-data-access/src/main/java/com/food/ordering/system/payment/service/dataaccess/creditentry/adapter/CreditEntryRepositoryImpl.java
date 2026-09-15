package com.food.ordering.system.payment.service.dataaccess.creditentry.adapter;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.food.ordering.system.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

  private final CreditEntryJpaRepository creditEntryJpaRepository;
  private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;
  private final EntityManager entityManager;

  @Override
  public CreditEntry save(CreditEntry creditEntry) {
    return creditEntryDataAccessMapper.toCreditEntry(
        creditEntryJpaRepository.save(
            creditEntryDataAccessMapper.toCreditEntryEntity(creditEntry)));
  }

  @Override
  public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
    return creditEntryJpaRepository
        .findByCustomerId(customerId.value())
        .map(creditEntryDataAccessMapper::toCreditEntry);
  }

  @Override
  public void detach(CustomerId customerId) {
    entityManager.detach(
        creditEntryJpaRepository.findByCustomerId(customerId.value()).orElseThrow());
  }
}
