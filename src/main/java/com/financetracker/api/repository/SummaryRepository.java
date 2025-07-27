package com.financetracker.api.repository;

import com.financetracker.api.dto.PieChart;
import com.financetracker.api.dto.TopCategoryExpenses;
import com.financetracker.api.dto.TopCategoryIncome;
import com.financetracker.api.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<Summary,Long> {

    // trả về Object[], phải map thủ công
    @Query(value = """
        SELECT 
            s.month,
            SUM(CASE WHEN s.type = 'INCOME' THEN s.amount ELSE 0 END) AS income,
            SUM(CASE WHEN s.type = 'EXPENSE' THEN s.amount ELSE 0 END) AS expense
        FROM summary s
        WHERE s.user_id = :userId AND s.year = :year
        GROUP BY s.month
        ORDER BY s.month
    """, nativeQuery = true)
    List<Object[]> getMonthlyReport(@Param("userId") Long userId, @Param("year") int year);

    // tự động map thông qua interface TopCategoryExpenses, để Spring Data JPA tự map
    @Query(value = """
            SELECT
                        sum(s.amount) as amount,
            			s.category_name as category,
                        s.icon_url as icon
                    FROM summary s
                    WHERE s.user_id = :userId AND s.year = :year AND s.month = :month AND s.type = "EXPENSE"
            		GROUP BY s.category_id
                   ORDER BY amount DESC
            """,nativeQuery = true)
    List<TopCategoryExpenses> getTopCategoryExpenses(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query(value = """
            SELECT
                        sum(s.amount) as amount,
            			s.category_name as category,
                        s.icon_url as icon
                    FROM summary s
                    WHERE s.user_id = :userId AND s.year = :year AND s.month = :month AND s.type = "INCOME"
            		GROUP BY s.category_id
                   ORDER BY amount DESC
            """,nativeQuery = true)
    List<TopCategoryIncome> getTopCategoryImcome(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query(value = """
            SELECT
                        sum(s.amount) as amount,
                        s.category_id,
            			s.category_name as category,
                        s.type as type,
                         STR_TO_DATE(CONCAT(year, '-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0')), '%Y-%m-%d') AS date,
                        s.emoji
                    FROM financetracker.summary s
                    WHERE s.user_id = :userId AND s.year = :year AND s.month = :month
                    GROUP BY s.category_id, s.category_name, s.type, s.emoji, s.year, s.month, s.day
            
                   ORDER BY date DESC
                   limit 3
            """,nativeQuery = true)
    List<Object[]> getCategory(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);


}

