package ru.otus.spring.integration.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Insect {
    protected int id;
    protected boolean alive;
}

