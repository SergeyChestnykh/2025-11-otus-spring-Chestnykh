package ru.otus.spring.integration.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Butterfly extends AbstractInsect {

    private int wingspan; // в мм

    private boolean canFly;

    public Butterfly(int id, boolean alive, int wingspan, boolean canFly) {
        super(id, alive);
        this.wingspan = wingspan;
        this.canFly = canFly;
    }
}
