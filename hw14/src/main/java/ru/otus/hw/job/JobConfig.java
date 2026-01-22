package ru.otus.hw.job;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.modelMongo.AuthorDocument;
import ru.otus.hw.modelMongo.BookDocument;
import ru.otus.hw.modelMongo.CommentDocument;
import ru.otus.hw.modelMongo.GenreDocument;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.CommentRepository;
import java.util.List;

@Configuration
@Slf4j
public class JobConfig {


    @Bean
    public ItemReader<Book> bookReader(EntityManagerFactory emf) {
        return new CustomBookReader(emf, 10);
    }

    @Bean
    public ItemProcessor<Book, BookDocument> bookProcessor(CommentRepository commentRepository) {

        return book -> {
            log.info("Processing book (id=%s): ".formatted(book.getId()));

            BookDocument doc = new BookDocument();
            doc.setId(book.getId().toString());
            doc.setTitle(book.getTitle());

            // Author
            Author author = book.getAuthor();
            doc.setAuthor(new AuthorDocument(
                    author.getId().toString(),
                    author.getFullName()
            ));

            // Genre
            Genre genre = book.getGenre();
            doc.setGenre(new GenreDocument(
                    genre.getId().toString(),
                    genre.getName()
            ));

            // в исходной модели комментарии не находятся внутри книги
            List<CommentDocument> comments = commentRepository
                    .findAllByBookId(book.getId())
                    .stream()
                    .map(c -> new CommentDocument(
                            c.getId().toString(),
                            c.getContent()
                    ))
                    .toList();

            doc.setComments(comments);

            return doc;
        };
    }

    @Bean
    public MongoItemWriter<BookDocument> bookWriter(MongoTemplate template) {
        MongoItemWriter<BookDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(template);
        writer.setCollection("books");
        return writer;
    }

    @Bean
    public Step migrateBooksStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Book> reader,
            ItemProcessor<Book, BookDocument> processor,
            MongoItemWriter<BookDocument> writer) {

        return new StepBuilder("migrateBooksStep", jobRepository)
                .<Book, BookDocument>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job migrateBooksJob(JobRepository jobRepository,
                               Step migrateBooksStep) {
        return new JobBuilder("migrateBooksJob", jobRepository)
                .start(migrateBooksStep)
                .preventRestart()
                .build();
    }
}
