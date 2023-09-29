package com.example.demo.controller;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.exceptions.NoDataFoundException;
import com.example.demo.model.Note;
import com.example.demo.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoteControllerSpringBootTests {

    @MockBean
    private NoteServiceImpl noteService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void canRetrieveByIdWhenExists() {
        //given
        given(noteService.getNoteById(1)).willReturn(new Note(1, "title1", "content1"));

        //when
        ResponseEntity<Note> NoteResponse = restTemplate.getForEntity("/v1/notes/1", Note.class);

        //then
        assertThat(NoteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(NoteResponse.getBody()).isEqualTo((new Note(1, "title1", "content1")));
    }

    @Test
    public void canRetrieveByIdWhenNotExists() {
        // given
        given(noteService.getNoteById(1)).willThrow(new NoDataFoundException());

        // when
        ResponseEntity<Note> NoteResponse = restTemplate.getForEntity("/v1/notes/1", Note.class);

        // then
        assertThat(NoteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(NoteResponse.getBody()).isEqualTo(null);
    }

    @Test
    public void canRetrieveNotes() {
        //given
        given(noteService.findNotesAll()).willReturn(List.of(new Note(1, "title1", "content1")
                , new Note(2, "title2", "content2")
                , new Note(3, "title3", "content3")));

        //when
        ResponseEntity<Note[]> NoteResponse = restTemplate.getForEntity("/v1/notes", Note[].class);

        //then
        assertThat(NoteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(NoteResponse.getBody()).isEqualTo(new Note[]{new Note(1, "title1", "content1")
                , new Note(2, "title2", "content2")
                , new Note(3, "title3", "content3")});
    }

    @Test
    public void canRetrieveNotesWhenEmpty() {
        // given
        given(noteService.findNotesAll()).willReturn(List.of());

        // when
        ResponseEntity<Note[]> NoteResponse = restTemplate.getForEntity("/v1/notes", Note[].class);

        // then
        assertThat(NoteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(NoteResponse.getBody()).isEqualTo(null);
    }

    @Test
    public void canCreateNote() {

        // given
        given(noteService.addNote(new NoteDTO("title1", "content1")))
                .willReturn(new Note(1, "title1", "content1"));

        // when
        ResponseEntity<Note> response = restTemplate.postForEntity("/v1/notes/",
                new NoteDTO("title1", "content1"),
                Note.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new Note(1,"title1","content1"));
    }

    @Test
    @Disabled
    public void canUpdateNote() {
    }

    @Test
    @Disabled
    public void canUpdateNoteWhenNotExists() {
    }

    @Test
    @Disabled
    public void canDeleteNoteWhenNotExists() {
    }

    // En este, ver la forma de no hacer el verify de Mockito y en su lugar qué se podría hacer.
    @Test
    @Disabled
    public void canDeleteNote() {
    }

    @Test
    @Disabled
    public void canJoinNoteWhenNotExists() {
    }

    @Test
    @Disabled
    public void canJoinNotes() {
    }
}
