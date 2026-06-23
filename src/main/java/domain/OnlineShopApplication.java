package domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@SpringBootApplication
public class OnlineShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineShopApplication.class, args);
    }


//    @Bean
//    public CommandLineRunner checkDatabase(DataSource dataSource) {
//        return args -> {
//            try (Connection conn = dataSource.getConnection()) {
//                log.info("=== DATABASE CONNECTION INFO ===");
//                log.info("JDBC URL: {}", conn.getMetaData().getURL());
//                log.info("Database: {}", conn.getMetaData().getDatabaseProductName());
//                log.info("Schema: {}", conn.getSchema());
//                log.info("Catalog: {}", conn.getCatalog());
//                log.info("================================");
//            }
//        };
//    }
}
