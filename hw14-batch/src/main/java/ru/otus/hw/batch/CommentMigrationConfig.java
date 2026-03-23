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
import ru.otus.hw.batch.converters.MongoJpaCommentConverter;
import ru.otus.hw.jpa.models.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
public class CommentMigrationConfig {

    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MongoTemplate mongoTemplate;
    private final EntityManagerFactory entityManagerFactory;
    private final MongoJpaCommentConverter mongoJpaCommentConverter;
    private final Map<String, Comment> mapMongoIdToComment = new HashMap<>();

    @Bean
    public Map<String, Comment> commentRelationsHolder() {
        return mapMongoIdToComment;
    }

    @Bean
    public Step commentMigrationStep() {
        return new StepBuilder("commentMigrationStep", jobRepository)
                .<ru.otus.hw.mongo.models.Comment, CommentMigrationItem>chunk(CHUNK_SIZE, transactionManager)
                .reader(commentMongoReader())
                .processor(commentProcessor())
                .writer(new MigrateJpaItemWriter(commentJpaWriter()))
                .build();
    }

    @Bean
    public MongoPagingItemReader<ru.otus.hw.mongo.models.Comment> commentMongoReader() {
        return new MongoPagingItemReaderBuilder<ru.otus.hw.mongo.models.Comment>()
                .name("commentMongoReader")
                .template(mongoTemplate)
                .targetType(ru.otus.hw.mongo.models.Comment.class)
                .jsonQuery("{}")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<ru.otus.hw.mongo.models.Comment, CommentMigrationItem> commentProcessor() {
        return mongoComment ->
                new CommentMigrationItem(mongoComment, mongoJpaCommentConverter.toCommentJpa(mongoComment));
    }

    @Bean
    public JpaItemWriter<Comment> commentJpaWriter() {
        return new JpaItemWriterBuilder<Comment>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    public class MigrateJpaItemWriter implements ItemWriter<CommentMigrationConfig.CommentMigrationItem> {

        private final JpaItemWriter<Comment> delegate;

        public MigrateJpaItemWriter(JpaItemWriter<Comment> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(Chunk<? extends CommentMigrationConfig.CommentMigrationItem> chunk) {
            List<Comment> authors = chunk.getItems().stream()
                    .map(CommentMigrationConfig.CommentMigrationItem::jpaComment)
                    .collect(Collectors.toList());

            delegate.write(new Chunk<>(authors));

            for (CommentMigrationConfig.CommentMigrationItem item : chunk.getItems()) {
                mapMongoIdToComment.put(
                        item.mongoComment().getId(),
                        item.jpaComment()
                );
            }
        }
    }

    public record CommentMigrationItem(ru.otus.hw.mongo.models.Comment mongoComment, Comment jpaComment) {
    }
}