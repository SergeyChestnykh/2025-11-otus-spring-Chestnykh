package ru.otus.spring.integration.services;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.otus.spring.integration.domain.Butterfly;
import ru.otus.spring.integration.domain.Caterpillar;

@Service
@Slf4j
@AllArgsConstructor
public class MetamorphosisServiceImpl implements MetamorphosisService {
    private final CaterpillarProvider caterpillarProvider;

    private final InsectGateway insectGateway;

    @Override
    public void startGenerateCaterpillarsLoop() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            int num = i + 1;
            pool.execute(() -> {
                Collection<Caterpillar> caterpillars = caterpillarProvider.getRandomCollection();
                log.info("{}, New Caterpillars({}): {}", num, caterpillars.size(),
                        caterpillars.stream().map(Caterpillar::getId)
                                .map(Object::toString)
                                .collect(Collectors.joining(", "))
                );
                Collection<Butterfly> butterflies = insectGateway.transform(caterpillars);
                if (butterflies == null) {
                    log.info("{}, No butterflies produced (all insects were dead)({})", num, num);
                    return;
                }
                log.info("{}, Ready Butterfly({}): {}", num, butterflies.size(),
                        butterflies.stream().map(Butterfly::getId)
                                .map(Object::toString)
                                .collect(Collectors.joining(", "))
                );
            });
            delay();
        }
    }

    private void delay() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
