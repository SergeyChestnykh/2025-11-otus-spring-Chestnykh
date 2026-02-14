package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    private String id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String title;

    private Author author;

    private List<Genre> genres;
}
