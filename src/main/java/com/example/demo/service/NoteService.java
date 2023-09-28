package com.example.demo.service;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.model.Note;

import java.util.List;

public interface NoteService {
    List<Note> findNotesAll();
    List<Note> findNotesByTitle(String title);
    Note addNote(NoteDTO note);
    Note updateNote(Integer id, NoteDTO note);
    void deleteNote(Integer id);
    Note getNoteById(Integer id);
    NoteDTO joinNotes(Integer id1, Integer id2);
}
