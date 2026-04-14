package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.BookRepository;

@RequiredArgsConstructor
@Service
public class MyHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            if (bookRepository.existsBy()) {
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