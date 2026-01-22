package ru.otus.hw.commands;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.MongoBookConverter;
import ru.otus.hw.modelMongo.BookDocument;
import ru.otus.hw.mongoRepositories.BookMongoRepository;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
public class JobCommands {

    private final JobLauncher jobLauncher;
    private final Job migrateBooksJob;

    private final BookMongoRepository bookMongoRepo;

    private final JobExplorer jobExplorer;


    public JobCommands(JobLauncher jobLauncher,
                       @Qualifier("migrateBooksJob") Job migrateBooksJob,
                       BookMongoRepository repo,
                       JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.migrateBooksJob = migrateBooksJob;
        this.bookMongoRepo = repo;
        this.jobExplorer = jobExplorer;
    }

    @ShellMethod(value = "Show migrated books", key = "mongo-books")
    public String showMongoBooks() {
        List<BookDocument> books = bookMongoRepo.findAll();

        var result = books
                .stream()
                .map(MongoBookConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
        return result;
    }

    @ShellMethod(key = "run-migration", value = "Run books migration job")
    public String runMigration() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(migrateBooksJob, params);

        return "Migration job started";
    }

    @ShellMethod(key = "restart", value = "Restart last failed migration")
    public String restartLastFailedJob() throws Exception {

        JobInstance lastInstance = jobExplorer.getLastJobInstance("migrateBooksJob");

        if (lastInstance == null) {
            return "No job migrateBooksJob executions";
        }

        JobExecution lastExecution = jobExplorer.getLastJobExecution(lastInstance);

        if (lastExecution == null) {
            return " execution for the last job instance is not found";
        }

        if (!lastExecution.getStatus().isUnsuccessful()) {
            return "The job should be in the FAILED or STOPPED state. Current state: " + lastExecution.getStatus();
        }

        JobExecution newExecution = jobLauncher.run(migrateBooksJob, lastExecution.getJobParameters());

        return "Job restarted. New ExecutionId = " + newExecution.getId() + ", Status = " + newExecution.getStatus();
    }
}
