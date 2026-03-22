package ru.otus.hw.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
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

@AllArgsConstructor
@Configuration
public class AuthorMigrationConfig {

    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MongoTemplate mongoTemplate;
    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public Step authorMigrationStep() {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<ru.otus.hw.mongo.models.Author, Author>chunk(CHUNK_SIZE, transactionManager)
                .reader(authorMongoReader())
                .processor(authorProcessor())
                .writer(authorJpaWriter())
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
    public ItemProcessor<ru.otus.hw.mongo.models.Author, Author> authorProcessor() {
        return authorMongo -> {
            Author author = new Author();
            author.setFullName(authorMongo.getFullName());
            return author;
        };
    }

    @Bean
    public JpaItemWriter<Author> authorJpaWriter() {
        return new JpaItemWriterBuilder<Author>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}