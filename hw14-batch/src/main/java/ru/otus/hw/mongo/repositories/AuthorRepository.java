package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.models.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
