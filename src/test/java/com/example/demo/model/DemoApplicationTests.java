package com.example.demo.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void testCreateNote() {

		Note note = new Note(1, "Title","Content");

		Assertions.assertEquals(note.getTitle(),"Title");
		Assertions.assertEquals(note.getContent(),"Content");
	}
}