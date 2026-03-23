package ru.otus.hw.batch;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.jpa.models.Comment;
import ru.otus.hw.jpa.repositories.AuthorRepository;
import ru.otus.hw.jpa.repositories.BookRepository;
import ru.otus.hw.jpa.repositories.CommentRepository;
import ru.otus.hw.jpa.repositories.GenreRepository;

import java.util.ArrayList;


@AllArgsConstructor
@Configuration
public class MigrationJobConfig {

    private static final Logger logger = LoggerFactory.getLogger(MigrationJobConfig.class);
    private final JobRepository jobRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;
    private final Step genreMigrationStep;
    private final Step authorMigrationStep;
    private final Step commentMigrationStep;
    private final Step bookMigrationStep;

    @Bean
    public Job runMigrationJob() {
        return new JobBuilder("migrationJob", jobRepository)
                .listener(jobExecutionListener())
                .start(genreMigrationStep)
                .next(authorMigrationStep)
                .next(bookMigrationStep)
                .next(commentMigrationStep)
                .build();
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(@NonNull JobExecution jobExecution) {
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    var genres = genreRepository.findAll();
                    var authors = authorRepository.findAll();
                    var books = bookRepository.findAll();
                    var comments = new ArrayList<Comment>();
                    books.forEach(book -> comments.addAll(commentRepository.findAllForBook(book.getId())));

                    logger.info("Migration job completed successfully");
                    logger.info("Total genres migrated to JPA: {}", genres.size());
                    logger.info("Total authors migrated to JPA: {}", authors.size());
                    logger.info("Total books migrated to JPA: {}", books.size());
                    logger.info("Total comments migrated to JPA: {}", comments.size());
                } else {
                    logger.error("Genre migration job failed with status: {}", jobExecution.getStatus());
                }
            }
        };
    }
}
