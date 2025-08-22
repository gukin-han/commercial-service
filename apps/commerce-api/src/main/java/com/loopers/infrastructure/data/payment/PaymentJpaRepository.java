package com.loopers.infrastructure.data.payment;

import com.loopers.domain.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    @Query("""
            select p from Payment p
            where p.status = 'PENDING'
              and p.updatedAt >= :cutoff
            """)
    Page<Payment> findPendingSince(@Param("cutoff") ZonedDateTime cutoff, Pageable pageable);
}
