package com.app.documentservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentContentRepository extends MongoRepository<DocumentContentRepository, String> {
}
