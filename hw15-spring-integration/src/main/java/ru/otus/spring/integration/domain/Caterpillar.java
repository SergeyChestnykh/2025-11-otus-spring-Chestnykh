package ru.otus.spring.integration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Caterpillar extends Insect {
    private int length; // в мм
    private int leafCountEaten;

    public Caterpillar(int id, boolean alive, int length, int leafCountEaten) {
        super(id, alive);
        this.length = length;
        this.leafCountEaten = leafCountEaten;
    }
    // поля специфичные для гусеницы
}
