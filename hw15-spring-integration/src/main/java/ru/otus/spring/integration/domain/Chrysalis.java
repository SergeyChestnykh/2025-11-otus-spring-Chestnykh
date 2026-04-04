package ru.otus.spring.integration.domain;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class Chrysalis extends AbstractInsect {

    private boolean isHardened;

    private LocalDate formedDate;

    public Chrysalis(int id, boolean alive, boolean isHardened, LocalDate formedDate) {
        super(id, alive);
        this.isHardened = isHardened;
        this.formedDate = formedDate;
    }
}
