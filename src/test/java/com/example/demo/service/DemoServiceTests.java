package com.example.demo.service;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class DemoServiceTests {

    @Mock
    private NoteRepository noteRepository;


    @InjectMocks
    private NoteServiceImpl underTest;

    @Captor
    ArgumentCaptor<Note> noteCaptor;

    @Test
    void testAddNote() {

        Note note = new Note(1, "title1", "content1");
        NoteDTO noteDTO = new NoteDTO("title1","content1");

        Mockito.when(noteRepository.save(note)).thenReturn(note);
        Note saved = underTest.addNote(noteDTO);

        Mockito.verify(noteRepository).save(noteCaptor.capture());

        Assertions.assertEquals(saved.getId(), 1);
        Assertions.assertEquals(saved.getContent(), "content1");
        Assertions.assertEquals(saved.getTitle(), "title1");
        Assertions.assertEquals(noteCaptor.getValue(),note);
    }

    @Test
    void testUpdateNote() {

        //update
        Note note = new Note(1, "title1", "content1");
        NoteDTO noteToUpdate = new NoteDTO("title2", "content2");

        Mockito.when(noteRepository.findById(1)).thenReturn(Optional.of(note));
        Mockito.when(noteRepository.save(argThat(new NoteMatcher(note)))).thenReturn(note);
        Note noteUpdated = underTest.updateNote(1, noteToUpdate);
        Mockito.verify(noteRepository).save(argThat(new NoteMatcher(note)));
    }

    @Test
    void testDeleteNote() {

        Note note = new Note(1, "title1", "content1");
        Mockito.when(noteRepository.findById(1)).thenReturn(Optional.of(note));
        doNothing().when(noteRepository).delete(note);
        underTest.deleteNote(note.getId());
        Mockito.verify(noteRepository).delete(note);
    }

    @Test
    void testGetNoteById() {
        Note note = new Note(1, "title1", "content1");
        Mockito.when(noteRepository.findById(1)).thenReturn(Optional.of(note));
        Note loaded = underTest.getNoteById(1);
        Mockito.verify(noteRepository).findById(1);
        Assertions.assertEquals(loaded, note);
    }

    @Test
    void testJoinNotes() {
        Note note1 = new Note(1, "title1", "content1");
        Note note2 = new Note(2, "title2", "content2");

        Mockito.when(noteRepository.findById(1)).thenReturn(Optional.of(note1));
        Mockito.when(noteRepository.findById(2)).thenReturn(Optional.of(note2));

        NoteDTO noteJoined = underTest.joinNotes(1, 2);

        Assertions.assertEquals(noteJoined.getTitle(), "joined:" + note1.getTitle() + " + " + note2.getTitle());
        Assertions.assertEquals(noteJoined.getContent(), note1.getContent() + " " + note2.getContent());
    }
}
