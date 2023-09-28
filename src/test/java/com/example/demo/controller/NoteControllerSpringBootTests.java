package com.example.demo.controller;

import com.example.demo.exceptions.NoDataFoundException;
import com.example.demo.model.Note;
import com.example.demo.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        given(noteService.getNoteById(1)).willReturn(new Note(1,"title1","content1"));

        //when
        ResponseEntity<Note> NoteResponse = restTemplate.getForEntity("/v1/notes/1",Note.class);

        //then
        assertThat(NoteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(NoteResponse.getBody()).isEqualTo((new Note(1,"title1","content1")));
    }

    @Test
    public void canRetrieveByIdWhenNotExists() {
        // given
        given(noteService.getNoteById(1)).willThrow(new NoDataFoundException());

        // when
        ResponseEntity<Note> NoteResponse = restTemplate.getForEntity("/v1/notes/1",Note.class);

        // then
        assertThat(NoteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(NoteResponse.getBody()).isEqualTo(null);
    }

}
