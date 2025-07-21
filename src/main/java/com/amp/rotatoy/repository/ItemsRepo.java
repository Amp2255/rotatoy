package com.amp.rotatoy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.amp.rotatoy.model.Items;


@Repository
public interface ItemsRepo extends MongoRepository<Items, String>{

    @Query("{ 'status' : ?0 }")
    Page <Items> findByStatus(String status,final Pageable pageable );


   @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Items> searchField(String searchField, Pageable pageable);


    @Query("{ 'status' : ?0 }")
    Optional <List<Items>> findListofItemsByStatus(String status);
    
}
