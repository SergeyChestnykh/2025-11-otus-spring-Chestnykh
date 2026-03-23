package ru.otus.hw.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.converters.MongoJpaBookConverter;
import ru.otus.hw.jpa.models.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
public class BookMigrationConfig {

    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MongoTemplate mongoTemplate;
    private final EntityManagerFactory entityManagerFactory;
    private final MongoJpaBookConverter mongoJpaBookConverter;
    private final Map<String, Book> mapMongoIdToBook = new HashMap<>();

    @Bean
    public Map<String, Book> bookRelationsHolder() {
        return mapMongoIdToBook;
    }

    @Bean
    public Step bookMigrationStep() {
        return new StepBuilder("bookMigrationStep", jobRepository)
                .<ru.otus.hw.mongo.models.Book, BookMigrationItem>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookMongoReader())
                .processor(bookProcessor())
                .writer(new MigrateJpaItemWriter(bookJpaWriter()))
                .build();
    }

    @Bean
    public MongoPagingItemReader<ru.otus.hw.mongo.models.Book> bookMongoReader() {
        return new MongoPagingItemReaderBuilder<ru.otus.hw.mongo.models.Book>()
                .name("bookMongoReader")
                .template(mongoTemplate)
                .targetType(ru.otus.hw.mongo.models.Book.class)
                .jsonQuery("{}")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<ru.otus.hw.mongo.models.Book, BookMigrationItem> bookProcessor() {
        return mongoBook -> new BookMigrationItem(mongoBook, mongoJpaBookConverter.toBookJpa(mongoBook));
    }

    @Bean
    public JpaItemWriter<Book> bookJpaWriter() {
        return new JpaItemWriterBuilder<Book>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }

    public class MigrateJpaItemWriter implements ItemWriter<BookMigrationItem> {

        private final JpaItemWriter<Book> delegate;

        public MigrateJpaItemWriter(JpaItemWriter<Book> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(Chunk<? extends BookMigrationItem> chunk) {
            List<Book> authors = chunk.getItems().stream()
                    .map(BookMigrationItem::jpaBook)
                    .collect(Collectors.toList());

            delegate.write(new Chunk<>(authors));

            for (BookMigrationItem item : chunk.getItems()) {
                mapMongoIdToBook.put(
                        item.mongoBook().getId(),
                        item.jpaBook()
                );
            }
        }
    }

    public record BookMigrationItem(ru.otus.hw.mongo.models.Book mongoBook, Book jpaBook) {
    }
}