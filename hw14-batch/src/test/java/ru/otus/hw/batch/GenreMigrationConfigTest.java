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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest
class GenreMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testGenreMigrationStep() {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("genreMigrationStep");
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        long read = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum();
        assertEquals(6, read);
        long written = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
        assertEquals(6, written);
    }

}