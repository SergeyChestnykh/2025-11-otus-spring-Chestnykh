package ru.otus.hw.hateoas;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Book;

@RepositoryRestResource(collectionResourceRel = "books", path = "books")
public interface BookRepositoryHateoas extends PagingAndSortingRepository<Book, Long> {
}