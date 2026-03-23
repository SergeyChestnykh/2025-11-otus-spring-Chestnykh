package ru.otus.hw.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.batch.core.ItemWriteListener;
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
import ru.otus.hw.jpa.models.Author;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
public class AuthorMigrationConfig {

    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final MongoTemplate mongoTemplate;

    private final EntityManagerFactory entityManagerFactory;

    private final Map<String, Author> mapMongoIdToAuthor = new HashMap<>();

    @Bean
    public Map<String, Author> authorRelationsHolder() {
        return mapMongoIdToAuthor;
    }


    @Bean
    public Step authorMigrationStep() {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<ru.otus.hw.mongo.models.Author, AuthorMigrationItem>chunk(CHUNK_SIZE, transactionManager)
                .reader(authorMongoReader())
                .processor(authorProcessor())
                .writer(new MigrateJpaItemWriter(authorJpaWriter()))
                .listener(new ItemWriteListener<Author>() {
                    @Override
                    public void afterWrite(@NonNull Chunk<? extends Author> items) {
                        System.out.println(mapMongoIdToAuthor);
                        items.forEach(author -> {
                            System.out.println("authorId: " + author.getId());
                        });
                        ItemWriteListener.super.afterWrite(items);
                    }
                })
                .build();
    }

    @Bean
    public MongoPagingItemReader<ru.otus.hw.mongo.models.Author> authorMongoReader() {
        return new MongoPagingItemReaderBuilder<ru.otus.hw.mongo.models.Author>()
                .name("authorMongoReader")
                .template(mongoTemplate)
                .targetType(ru.otus.hw.mongo.models.Author.class)
                .jsonQuery("{}")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<ru.otus.hw.mongo.models.Author, AuthorMigrationItem> authorProcessor() {
        return item -> {
            Author author = new Author();
            author.setFullName(item.getFullName());
            return new AuthorMigrationItem(item, author);
        };
    }

    @Bean
    public JpaItemWriter<Author> authorJpaWriter() {
        return new JpaItemWriterBuilder<Author>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }

    public class MigrateJpaItemWriter implements ItemWriter<AuthorMigrationItem> {

        private final JpaItemWriter<Author> delegate;

        public MigrateJpaItemWriter(JpaItemWriter<Author> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(Chunk<? extends AuthorMigrationItem> chunk) {
            List<Author> authors = chunk.getItems().stream()
                    .map(AuthorMigrationItem::jpaAuthor)
                    .collect(Collectors.toList());

            delegate.write(new Chunk<>(authors));

            for (AuthorMigrationItem item : chunk.getItems()) {
                mapMongoIdToAuthor.put(
                        item.mongoAuthor().getId(),
                        item.jpaAuthor()
                );
            }
        }
    }

    public record AuthorMigrationItem(ru.otus.hw.mongo.models.Author mongoAuthor, Author jpaAuthor) {
    }
}