package com.example.demo.controller;

import com.example.demo.DTO.NoteDTO;
import com.example.demo.config.ErrorHandlerConfig;
import com.example.demo.exceptions.GeneralServiceException;
import com.example.demo.exceptions.NoDataFoundException;
import com.example.demo.model.Note;
import com.example.demo.service.impl.NoteServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class NoteControllerTests {

    private MockMvc mvc;

    @Mock
    private NoteServiceImpl noteServiceImpl;

    @InjectMocks
    private NoteController noteController;

    private JacksonTester<Note> jsonNote;
    private JacksonTester<List<Note>> jsonListNote;
    private JacksonTester<NoteDTO> jsonNoteDTO;

    @BeforeEach
    public void setup() {
        // We would need this line if we would not use the MockitoExtension
        // MockitoAnnotations.initMocks(this);
        // Here we can't use @AutoConfigureJsonTesters because there isn't a Spring context
        JacksonTester.initFields(this, new ObjectMapper());

        //MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(noteController)
                .setControllerAdvice(new ErrorHandlerConfig())
                .build();
    }

    @Test
    public void canRetrieveByIdWhenExists() throws Exception {
        // given
        given(noteServiceImpl.getNoteById(2)).willReturn(new Note(2, "Title2", "Content2"));

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/v1/notes/2")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonNote.write(
                new Note(2, "Title2", "Content2")).getJson()
        );
    }

    /*
        Sobre este caso de test es necesario decir una cosa. La excepción que se está devolviendo es
        GeneralServiceException, pero si vamos a la clase que la genera, vemos que originalmente se trata de una
        NoDataFoundException, y lo que debería devolverse es un NO_CONTENT. Esto no se hace así porque se copió
        de una tutorial de internet en el que la gestión de las excepciones se hacía así. Pero en este caso
        vemos que podría hacerse de forma diferente, devolviendo un NO_CONTENT y una excepción de NoDataFound.
     */

    @Test
    public void canRetrieveByIdWhenNotExists() throws Exception {
        // given
        given(noteServiceImpl.getNoteById(2)).willThrow(new GeneralServiceException());

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/v1/notes/2")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void canRetrieveNotes() throws Exception {
        // given
        given(noteServiceImpl.findNotesAll()).willReturn(List.of(new Note(1, "title1", "content1"),
                new Note(2, "title2", "content2"),
                new Note(3, "title3", "content3")));

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/v1/notes")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonListNote.write(List.of(new Note(1, "title1", "content1"),
                new Note(2, "title2", "content2"),
                new Note(3, "title3", "content3"))).getJson());
    }

    @Test
    public void canRetrieveNotesWhenEmpty() throws Exception {
        // given
        given(noteServiceImpl.findNotesAll()).willReturn(List.of());

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/v1/notes")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getContentAsString()).isEqualTo("");
    }

    @Test
    public void canCreateNote() throws Exception {
        // given
        given(noteServiceImpl.addNote(new NoteDTO("title1", "content1")))
                .willReturn(new Note(1, "title1", "content1"));

        // when
        MockHttpServletResponse response = mvc.perform(
                        post("/v1/notes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(jsonNoteDTO.write(new NoteDTO("title1", "content1"))
                                        .getJson()))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString())
                .isEqualTo(jsonNote.write(new Note(1, "title1", "content1")).getJson());
    }

    @Test
    public void canUpdateNote() throws Exception {

        // given
        given(noteServiceImpl.updateNote(1, new NoteDTO("title1", "content1")))
                .willReturn(new Note(1, "title1", "content1"));

        // when
        MockHttpServletResponse response = mvc.perform(
                        put("/v1/notes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(jsonNoteDTO.write(new NoteDTO("title1", "content1")).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .isEqualTo(jsonNote.write(new Note(1, "title1", "content1")).getJson());
    }

    @Test
    public void canUpdateNoteWhenNotExists() throws Exception {
        // given
        given(noteServiceImpl.updateNote(2, new NoteDTO("title2", "content2")))
                .willThrow(new NoDataFoundException());

        // then
        MockHttpServletResponse response = mvc.perform(
                put("/v1/notes/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNoteDTO.write(new NoteDTO("title2", "content2")).getJson())
        ).andReturn().getResponse();

        // when
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /*
        En estos tests a continuación he mezclado BDDMockito con Mockito normal.
        Ver si hay alguna manera de hacerlos solo con BDDMockito y AssertJ
        En general, ver como se hace para tratar las excepciones con BDDMockito y AssertJ
        UPDATE: No hace falta aquí ya
     */

    //En este, ver si hay alguna forma de capturar la excepción en el AssertJ:
    //UPDATE: No hace falta capturar la excepción porque ya me la captura el ErrorHandlerConfig (@ControllerAdvice)
    @Test
    public void canDeleteNoteWhenNotExists() throws Exception {
        // given
        willThrow(new NoDataFoundException()).given(noteServiceImpl).deleteNote(1);

        // when
        MockHttpServletResponse response = null;
        response = mvc.perform(
                    delete("/v1/notes/1")
            ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // En este, ver la forma de no hacer el verify de Mockito y en su lugar qué se podría hacer.
    @Test
    public void canDeleteNote() throws Exception {

        //given
        doNothing().when(noteServiceImpl).deleteNote(1);

        //when
        MockHttpServletResponse response = mvc.perform(
                    delete("/v1/notes/1")
            ).andReturn().getResponse();

        //then
        Mockito.verify(noteServiceImpl).deleteNote(1);
    }

    @Test
    public void canJoinNoteWhenNotExists() throws Exception {
        //given
        //willThrow(new NoDataFoundException()).given(noteServiceImpl).joinNotes(0,0);
        //org.mockito.exceptions.misusing.UnnecessaryStubbingException: Stub is not necessary

        //when
        MockHttpServletResponse response = mvc.perform(
                get("/v1/notes/join?id1=0&id2=0")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void canJoinNotes() throws Exception {

        // given
        given(noteServiceImpl.joinNotes(1,2)).willReturn(new NoteDTO("joined:title1 + title2","content1 content2"));

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/v1/notes/join?id1=1&id2=2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonNoteDTO.write(new NoteDTO("joined:title1 + title2","content1 content2")).getJson());
    }
}
