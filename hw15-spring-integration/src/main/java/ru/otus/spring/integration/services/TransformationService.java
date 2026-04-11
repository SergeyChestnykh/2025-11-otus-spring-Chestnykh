package ru.otus.spring.integration.services;

import org.springframework.stereotype.Service;
import ru.otus.spring.integration.domain.Butterfly;
import ru.otus.spring.integration.domain.Caterpillar;
import ru.otus.spring.integration.domain.Chrysalis;

import java.time.LocalDate;

@Service
public class TransformationService {
    public Chrysalis toChrysalis(Caterpillar caterpillar) {
        return new Chrysalis(
                caterpillar.getId(),
                caterpillar.isAlive(),
                true,
                LocalDate.now()
        );
    }

    public Butterfly toButterfly(Chrysalis chrysalis) {
        return new Butterfly(
                chrysalis.getId(),
                chrysalis.isAlive(),
                15,
                true
        );
    }
}
