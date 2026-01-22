package ru.otus.hw.mongoRepositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.modelMongo.BookDocument;


public interface BookMongoRepository extends MongoRepository<BookDocument, String> {

}
