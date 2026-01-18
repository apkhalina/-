package com.library.library_system.repository;

import com.library.library_system.entity.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий для работы с выдачами книг.
 */
@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    @Query("SELECT bl FROM BookLoan bl WHERE bl.returnDate IS NULL")
    List<BookLoan> findActiveLoans();

    @Query("SELECT bl FROM BookLoan bl WHERE bl.dueDate < :currentDate AND bl.returnDate IS NULL")
    List<BookLoan> findOverdueLoans(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT DISTINCT bl FROM BookLoan bl " +
            "LEFT JOIN FETCH bl.book " +
            "LEFT JOIN FETCH bl.reader " +
            "LEFT JOIN FETCH bl.librarian " +
            "ORDER BY bl.loanDate DESC")
    List<BookLoan> findAllWithDetails();

    @Query("SELECT bl FROM BookLoan bl WHERE bl.book.id = :bookId")
    List<BookLoan> findByBookId(@Param("bookId") Long bookId);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.reader.id = :readerId")
    List<BookLoan> findByReaderId(@Param("readerId") Long readerId);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.librarian.id = :librarianId")
    List<BookLoan> findByLibrarianId(@Param("librarianId") Long librarianId);

    @Query("SELECT CASE WHEN COUNT(bl) > 0 THEN true ELSE false END " +
            "FROM BookLoan bl WHERE bl.book.id = :bookId AND bl.returnDate IS NULL")
    boolean existsActiveLoanByBookId(@Param("bookId") Long bookId);

    @Query("SELECT CASE WHEN COUNT(bl) > 0 THEN true ELSE false END " +
            "FROM BookLoan bl WHERE bl.reader.id = :readerId AND bl.returnDate IS NULL")
    boolean existsActiveLoanByReaderId(@Param("readerId") Long readerId);
}