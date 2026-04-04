package ru.otus.hw.batch.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.jpa.models.Author;
import ru.otus.hw.jpa.models.Book;
import ru.otus.hw.jpa.models.Genre;

@Configuration
public class RelationCacheConfig {

    @Bean
    public MongoIdRelationCache<Author> authorRelationsCache() {
        return new MongoIdRelationCache<>();
    }

    @Bean
    public MongoIdRelationCache<Book> bookRelationsCache() {
        return new MongoIdRelationCache<>();
    }

    @Bean
    public MongoIdRelationCache<Genre> genreRelationsCache() {
        return new MongoIdRelationCache<>();
    }
}
