package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.models.Genre;

public interface GenreRepository extends MongoRepository<Genre, String> {
}
