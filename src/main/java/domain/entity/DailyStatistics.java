package domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="daily_statistics", uniqueConstraints = @UniqueConstraint(columnNames = "date"))
public class DailyStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private Long totalOrders = 0L;

    @Column(nullable = false)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal averageOrderValue = BigDecimal.ZERO;
}
