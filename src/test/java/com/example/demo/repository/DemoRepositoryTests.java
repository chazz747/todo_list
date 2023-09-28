package com.example.demo.repository;

import com.example.demo.model.Note;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class DemoRepositoryTests {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void testFindByTitleContainingFound() {

        Note note1 = new Note(1, "Title1", "Content1");
        Note note2 = new Note(2, "Title2", "Content2");
        Note note3 = new Note(3, "Title3", "Content3");
        Note saved = null;

        saved = noteRepository.save(note1);
        saved = noteRepository.save(note2);
        saved = noteRepository.save(note3);

        List<Note> notes = noteRepository.findByTitleContaining("Title2");

        Assertions.assertEquals(notes.get(0), note2);
    }

    @Test
    void testFindByTitleContainingNotFound() {

        Note note1 = new Note(1, "Title1", "Content1");
        Note note2 = new Note(2, "Title2", "Content2");
        Note note3 = new Note(3, "Title3", "Content3");
        Note guardado = null;

        guardado = noteRepository.save(note1);
        guardado = noteRepository.save(note2);
        guardado = noteRepository.save(note3);

        List<Note> notes = noteRepository.findByTitleContaining("TitleXYZ");

        Assertions.assertEquals(notes.isEmpty(), true);
    }

    @Test
    void testFindByContentFound() {

        Note note1 = new Note(1, "Title1", "Content1");
        Note note2 = new Note(2, "Title2", "Content2Content5");
        Note note3 = new Note(3, "Title3", "Content3");
        Note saved = null;

        saved = noteRepository.save(note1);
        saved = noteRepository.save(note2);
        saved = noteRepository.save(note3);

        List<Note> notes = noteRepository.findByContent("Content2");

        Assertions.assertEquals(notes.get(0), note2);
    }

    @Test
    void testFindByContentNotFound() {

        Note note1 = new Note(1, "Title1", "Content1");
        Note note2 = new Note(2, "Title2", "Content2");
        Note note3 = new Note(3, "Title3", "Content3");
        Note saved = null;

        saved = noteRepository.save(note1);
        saved = noteRepository.save(note2);
        saved = noteRepository.save(note3);

        List<Note> notes = noteRepository.findByContent("ContentXYZ");

        Assertions.assertEquals(notes.isEmpty(), true);
    }
}