package com.example.demo.controller;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.model.Note;
import com.example.demo.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<List<Note>> getNotes(@RequestParam(value = "title", required = false, defaultValue = "") String title) {

        List<Note> notes = null;

        if (title.isEmpty()) {
            notes = noteService.findNotesAll();
        } else {
            notes = noteService.findNotesByTitle(title);
        }

        if (notes.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(notes);
        }
    }

    @GetMapping({"{id}"})
    public ResponseEntity<Note> getNoteById(@PathVariable(value = "id") Integer id) {
        Note noteLoaded = noteService.getNoteById(id);

        if (noteLoaded == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(noteLoaded);
        }
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody NoteDTO noteToAdd) {
        Note noteAdded = noteService.addNote(noteToAdd);

        if (noteAdded == null) {
            return ResponseEntity.internalServerError().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(noteAdded);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") Integer id, @RequestBody NoteDTO noteToUpdate) {
        Note updated = noteService.updateNote(id, noteToUpdate);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updated);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Note> deleteNote(@PathVariable("id") Integer id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(null);
    }

    //Extra actions
    @GetMapping("/join")
    public ResponseEntity<NoteDTO> joinNotes(@RequestParam(value = "id1", required = true, defaultValue = "0") Integer id1,
                                             @RequestParam(value = "id2", required = true, defaultValue = "0") Integer id2) {
        if (id1 == 0 || id2 == 0) {
            return ResponseEntity.noContent().build();
        } else {
            NoteDTO joined = noteService.joinNotes(id1, id2);
            return ResponseEntity.ok(joined);
        }
    }
}
