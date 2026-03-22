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
import ru.otus.hw.jpa.models.Genre;

import java.util.HashMap;

@AllArgsConstructor
@Configuration
public class GenreMigrationConfig {

    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MongoTemplate mongoTemplate;
    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public Step genreMigrationStep() {
        return new StepBuilder("genreMigrationStep", jobRepository)
                .<ru.otus.hw.mongo.models.Genre, Genre>chunk(CHUNK_SIZE, transactionManager)
                .reader(genreMongoReader())
                .processor(genreProcessor())
                .writer(genreJpaWriter())
                .build();
    }

    @Bean
    public MongoPagingItemReader<ru.otus.hw.mongo.models.Genre> genreMongoReader() {
        return new MongoPagingItemReaderBuilder<ru.otus.hw.mongo.models.Genre>()
                .name("genreMongoReader")
                .template(mongoTemplate)
                .targetType(ru.otus.hw.mongo.models.Genre.class)
                .jsonQuery("{}")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<ru.otus.hw.mongo.models.Genre, Genre> genreProcessor() {
        return genreMongo -> {
            Genre genre = new Genre();
            genre.setName(genreMongo.getName());
            return genre;
        };
    }

    @Bean
    public JpaItemWriter<Genre> genreJpaWriter() {
        return new JpaItemWriterBuilder<Genre>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}