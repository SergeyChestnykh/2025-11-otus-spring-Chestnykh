package ru.otus.spring.integration.services;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import ru.otus.spring.integration.domain.Caterpillar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CaterpillarProvider {

    private final List<Caterpillar> all = generate();

    public Caterpillar getRandom() {
        Random random = new Random();
        return all.stream()
                .skip(random.nextInt(all.size()))
                .findFirst()
                .orElse(null);
    }

    public Collection<Caterpillar> getRandomCollection() {
        return all.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list.stream().limit(RandomUtils.nextInt(1, 5)).toList();
                        }
                ));
    }

    private List<Caterpillar> generate() {
        List<Caterpillar> caterpillars = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            int length = 10 + random.nextInt(91); // длина 10–100 мм
            int leavesEaten = random.nextInt(21); // съедено 0–20 листьев
            boolean alive = random.nextBoolean();

            Caterpillar c = new Caterpillar(i, alive, length, leavesEaten);
            caterpillars.add(c);
        }

        // Вывод для проверки
        for (Caterpillar c : caterpillars) {
            System.out.println("Caterpillar " + c.getId() +
                    " | alive: " + c.isAlive() +
                    " | length: " + c.getLength() +
                    " | leaves eaten: " + c.getLeafCountEaten());
        }
        return caterpillars;
    }
}