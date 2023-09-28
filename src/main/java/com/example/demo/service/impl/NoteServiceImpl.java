package com.example.demo.service.impl;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.exceptions.GeneralServiceException;
import com.example.demo.exceptions.NoDataFoundException;
import com.example.demo.exceptions.ValidateServiceException;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.NoteService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    @Autowired
    private NoteRepository noteRepository;

    @Override
    @Transactional
    public List<Note> findNotesAll() {
        try {
            List<Note> notes = noteRepository.findAll();
            return notes;
        } catch (NoDataFoundException e) {
            logger.info(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<Note> findNotesByTitle(String title) {
        try {
            List<Note> notes = noteRepository.findByTitleContaining(title);
            return notes;
        } catch (ValidateServiceException | NoDataFoundException e) {
            logger.info(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Note getNoteById(Integer id) {

        try {
            Note loaded = noteRepository.findById(id).orElseThrow(()
                    -> new NoDataFoundException("No existe el registro con ese ID."));
            return loaded;
        } catch (ValidateServiceException | NoDataFoundException e) {
            logger.info(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Note addNote(NoteDTO note) {

        Note noteToSave = new Note();
        noteToSave.setContent(note.getContent());
        noteToSave.setTitle(note.getTitle());
        try {
            Note saved = noteRepository.save(noteToSave);
            return saved;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Note updateNote(Integer id, NoteDTO note) {
        try {
            Note saved = null;
            Note searched = noteRepository.findById(id).orElseThrow(
                    () -> new NoDataFoundException("No existe el registro con ese ID."));

            searched.setContent(note.getContent());
            searched.setTitle(note.getTitle());
            saved = noteRepository.save(searched);
            return saved;
        } catch (ValidateServiceException| NoDataFoundException e) {
            logger.info(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteNote(Integer id) {
        try {
            Note toBeDeleted = noteRepository.findById(id).orElseThrow(
                    () -> new NoDataFoundException("No existe el registro con ese ID."));
            noteRepository.delete(toBeDeleted);
        } catch (ValidateServiceException|NoDataFoundException e) {
            logger.info(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public NoteDTO joinNotes(Integer id1, Integer id2) {
        try {
            Note loaded1 = noteRepository.findById(id1).orElseThrow(()
                    -> new NoDataFoundException("No existe el registro con ese ID."));
            Note loaded2 = noteRepository.findById(id2).orElseThrow(()
                    -> new NoDataFoundException("No existe el registro con ese ID."));
            NoteDTO joined = new NoteDTO();
            joined.setTitle("joined:" + loaded1.getTitle() + " + " + loaded2.getTitle());
            joined.setContent(loaded1.getContent() + " " + loaded2.getContent());
            return joined;

        } catch (ValidateServiceException|NoDataFoundException e) {
            logger.info(e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralServiceException(e.getMessage(), e);
        }
    }
}
