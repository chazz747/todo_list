package com.example.demo.repository;

import com.example.demo.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note,Integer> {
    List<Note> findByTitleContaining(String title);

    @Query("select n from Note n where n.content LIKE CONCAT('%',?1,'%')")
    List<Note> findByContent(String contentSearched);
}
