package domain.service;

import domain.entity.DailyStatistics;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import java.time.LocalDate;

public interface StatisticsService {

    void recordOrder(Long orderId, Long accountId, BigDecimal amount);
    DailyStatistics getDailyStatistics(LocalDate date);
    List<DailyStatistics> getStatisticsRange(LocalDate from, LocalDate to); //за определенный период
    DailyStatistics getTodayStatistics();
}
