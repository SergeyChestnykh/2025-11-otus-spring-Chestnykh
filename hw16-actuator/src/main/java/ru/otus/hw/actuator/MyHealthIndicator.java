package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@RequiredArgsConstructor
@Component
public class MyHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            if (bookRepository.findAll().isEmpty()) {
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