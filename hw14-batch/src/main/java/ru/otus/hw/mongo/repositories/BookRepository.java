package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.models.Book;

public interface BookRepository extends MongoRepository<Book, String> {
}
