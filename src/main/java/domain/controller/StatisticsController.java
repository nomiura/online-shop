package domain.controller;

import domain.entity.DailyStatistics;
import domain.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // GET /api/statistics/today
    @GetMapping("/today")
    public DailyStatistics getTodayStatistics() {
        return statisticsService.getTodayStatistics();
    }

    // GET /api/statistics/2026-01-15
    @GetMapping("/{date}")
    public DailyStatistics getByDate(
            @PathVariable @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date) {
        return statisticsService.getDailyStatistics(date);
    }

    // GET /api/statistics?from=2024-01-01&to=2024-01-31
    @GetMapping
    public List<DailyStatistics> getByRange( //iso = DateTimeFormat.ISO.DATE - YYYY-MM-DD 2026-06-15 время
                                             // и часовой пояс игнорируются
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return statisticsService.getStatisticsRange(from, to);
    }
}
//curl -X POST http://localhost:8081/api/orders \
//        -H "Content-Type: application/json" \
//        -d '{"userId": 1, "totalAmount": 100.0, "email": "a@test.com"}'
//
//curl -X POST http://localhost:8081/api/orders \
//        -H "Content-Type: application/json" \
//        -d '{"userId": 2, "totalAmount": 200.0, "email": "b@test.com"}'
//
//curl -X POST http://localhost:8081/api/orders \
//        -H "Content-Type: application/json" \
//        -d '{"userId": 3, "totalAmount": 300.0, "email": "c@test.com"}'


//curl http://localhost:8081/api/statistics/today