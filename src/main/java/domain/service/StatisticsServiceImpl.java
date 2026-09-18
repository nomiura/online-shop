package domain.service;

import domain.entity.DailyStatistics;
import domain.repository.DailyStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final DailyStatisticsRepository statisticsRepository;

    //записать новый заказ в статистику (вызывается из consumer)
    @Override
    @Transactional
    public void recordOrder(Long orderId, Long accountId, BigDecimal amount) {
        LocalDate today = LocalDate.now();
        log.info("Записываем заказ {} в статистику за {}", orderId, today);

        DailyStatistics dailyStatistics = statisticsRepository.findByDateForUpdate(today)
                .orElseGet(() -> {
                    DailyStatistics newStats = new DailyStatistics();
                    newStats.setDate(today);
                    newStats.setTotalOrders(0L);
                    newStats.setAverageOrderValue(BigDecimal.ZERO);
                    newStats.setTotalRevenue(BigDecimal.ZERO);
                    return newStats;
                });

        //обновляем счетчики
        dailyStatistics.setTotalOrders(dailyStatistics.getTotalOrders() + 1L);
        dailyStatistics.setTotalRevenue(dailyStatistics.getTotalRevenue().add(amount));
        dailyStatistics.setAverageOrderValue(dailyStatistics.getTotalRevenue()
                .divide(BigDecimal.valueOf(dailyStatistics.getTotalOrders()), 4, RoundingMode.HALF_EVEN));

        statisticsRepository.save(dailyStatistics);

        log.info("Статистика обновлена: заказов={}, выручка={}, средний чек={}",
                dailyStatistics.getTotalOrders(),
                dailyStatistics.getTotalRevenue(),
                dailyStatistics.getAverageOrderValue());

    }

    @Override
    public DailyStatistics getDailyStatistics(LocalDate date) {
        return statisticsRepository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("Статистика за " + date + " не найдена"));
    }

    @Override
    public List<DailyStatistics> getStatisticsRange(LocalDate from, LocalDate to) {
        return statisticsRepository.findAll().stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .toList();
    }

    @Override
    public DailyStatistics getTodayStatistics() {
        return statisticsRepository.findByDate(LocalDate.now())
                .orElseGet(() -> {
                    DailyStatistics emptyStats = new DailyStatistics();
                    emptyStats.setDate(LocalDate.now());
                    emptyStats.setTotalOrders(0L);
                    emptyStats.setAverageOrderValue(BigDecimal.ZERO);
                    emptyStats.setTotalRevenue(BigDecimal.ZERO);
                    return emptyStats;
                });
    }
}
