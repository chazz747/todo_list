package com.example.demo.service;

import com.example.demo.model.Note;
import org.mockito.ArgumentMatcher;

public class NoteMatcher implements ArgumentMatcher<Note> {

    private final Note left;

    public NoteMatcher(Note note) {
        left = note;
    }

    @Override
    public boolean matches(Note right) {
        return left.getContent().equals(right.getContent()) &&
                left.getTitle().equals(right.getTitle()) &&
                left.getId().equals(right.getId());
    }

    @Override
    public Class<?> type() {
        return ArgumentMatcher.super.type();
    }
}
