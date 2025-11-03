package com.projekt.wypozyczalnia.dao;

import com.projekt.wypozyczalnia.models.Loan;
import com.projekt.wypozyczalnia.models.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {

    Page<Loan> findAllByMemberId(UUID memberId, Pageable pageable);

    boolean existsByBookIdAndStatusIn(UUID bookId, Collection<LoanStatus> statuses);

    boolean existsByMemberIdAndBookIdAndStatusIn(UUID memberId, UUID bookId, Collection<LoanStatus> statuses);

    boolean existsByMemberIdAndStatusIn(UUID memberId, Collection<LoanStatus> statuses);
}
