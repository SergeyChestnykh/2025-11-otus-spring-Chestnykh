package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Override
    @NonNull
    List<Genre> findAll();

    List<Genre> findAllByIdIn(Set<Long> ids);
}
