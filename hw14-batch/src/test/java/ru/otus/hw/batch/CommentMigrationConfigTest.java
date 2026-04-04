package ru.otus.hw.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.batch.cache.MongoIdRelationCache;
import ru.otus.hw.jpa.models.Comment;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest
class CommentMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoIdRelationCache<Comment> mongoIdRelationCache;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testCommentMigrationStep() {
        assertEquals(BatchStatus.COMPLETED, jobLauncherTestUtils.launchStep("genreMigrationStep").getStatus());
        assertEquals(BatchStatus.COMPLETED, jobLauncherTestUtils.launchStep("authorMigrationStep").getStatus());
        assertEquals(BatchStatus.COMPLETED, jobLauncherTestUtils.launchStep("bookMigrationStep").getStatus());
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("commentMigrationStep");
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        long read = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum();
        assertEquals(3, read);
        long written = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
        assertEquals(3, written);
        assertEquals(3, mongoIdRelationCache.getAll().size());
    }

}