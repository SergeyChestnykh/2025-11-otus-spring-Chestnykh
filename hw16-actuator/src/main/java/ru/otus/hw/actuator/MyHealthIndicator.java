package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@Component
public class MyHealthIndicator implements HealthIndicator {

    private final BookService bookService;

    @Override
    public Health health() {
        try {
            if (bookService.isExists()) {
                return Health.up().build();
            } else {
                return Health.down()
                        .withDetail("reason", "Book database is empty.")
                        .build();
            }
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}