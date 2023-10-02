package com.example.demo.controller;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.exceptions.NoDataFoundException;
import com.example.demo.model.Note;
import com.example.demo.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;

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

        NoteDTO noteDTO = new NoteDTO("title1", "content1");

        // when
        ResponseEntity<Note> response = restTemplate.postForEntity("/v1/notes",
                noteDTO, Note.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(new Note(1, "title1", "content1"));
    }

    @Test
    public void canUpdateNote() {
        //given
        given(noteService.updateNote(1, new NoteDTO("title1", "content1")))
                .willReturn(new Note(1, "title1", "content1"));
        //when
        //This following code returns void, and because of that we will use exchange instead
        //restTemplate.put("/v1/notes/1",new NoteDTO("title1","content1"));

        ResponseEntity<Note> exchange = restTemplate.exchange(
                "/v1/notes/1", HttpMethod.PUT,
                new HttpEntity<>(new NoteDTO("title1", "content1")),
                Note.class);

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody()).isEqualTo(new Note(1, "title1", "content1"));
    }

    @Test
    public void canUpdateNoteWhenNotExists() {

        // given
        given(noteService.updateNote(1,new NoteDTO("title1","content1")))
                .willThrow(new NoDataFoundException());

        // when
        ResponseEntity<Note> exchange = restTemplate.exchange(
                "/v1/notes/1", HttpMethod.PUT,
                new HttpEntity<>(new NoteDTO("title1", "content1")),
                Note.class);

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exchange.getBody()).isEqualTo(null);
    }

    @Test
    public void canDeleteNoteWhenNotExists() {

        // given
        willThrow(new NoDataFoundException()).given(noteService).deleteNote(1);

        // when
        restTemplate.delete("/v1/notes/{id}", 1);

        // then
        Mockito.verify(noteService).deleteNote(1);
    }

    @Test
    public void canDeleteNote() {
        //given
        doNothing().when(noteService).deleteNote(1);

        //when
        restTemplate.delete("/v1/notes/{id}", 1);

        //then
        Mockito.verify(noteService).deleteNote(1);
    }

    @Test
    public void canJoinNoteWhenNotExists() {
        // given
        willThrow(new NoDataFoundException()).given(noteService).joinNotes(0,0);

        // when
        ResponseEntity<NoteDTO> response =
                restTemplate.getForEntity("/v1/notes/join?id1=0&id2=0", NoteDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isEqualTo(null);
    }

    @Test
    public void canJoinNotes() {

        // given
        given(noteService.joinNotes(1,2)).willReturn(new NoteDTO("joined:title1 + title2","content1 content2"));

        // when
        ResponseEntity<NoteDTO> response = restTemplate.getForEntity("/v1/notes/join?id1=1&id2=2", NoteDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new NoteDTO("joined:title1 + title2","content1 content2"));

    }
}

