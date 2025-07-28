package com.fffc.csvmaker.integration;

import com.fffc.csvmaker.controller.StandaloneController;
import com.fffc.csvmaker.service.CsvService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@ExtendWith(SpringExtension.class)
@Import(StandaloneController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StandaloneIntegrationTest {
    @Autowired
    private CsvService csvService;

    @Autowired
    private final MockMvcTester mockMvc = MockMvcTester.of(
            new StandaloneController(csvService)
    );

    @Test
    void testProcessCsv() throws Exception {
        MockMultipartFile metadataFile = new MockMultipartFile("metadataFile", "metadata", "application/octet-stream", """
                Date de naissance,10,date
                Prénom,15,chaîne
                Nom de famille,15,chaîne
                Poids,5,numérique
                """.getBytes());
        MockMultipartFile dataFile = new MockMultipartFile("dataFile", "data", "application/octet-stream", """
                1970-01-01John Smith 81.5
                1975-01-31Jane Doe 61.1
                1988-11-28Bob Big 102.4
                """.getBytes());

        MvcResult result = mockMvc.perform(multipart("/api/v1/csv-maker/standalone/")
                .file(metadataFile)
                .file(dataFile)).getMvcResult();

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("text/plain;charset=UTF-8", result.getResponse().getContentType());
        assertEquals("""
                Date de naissance,Prénom,Nom de famille,Poids
                01/01/1970,John,Smith,81.5
                31/01/1975,Jane,Doe,61.1
                28/11/1988,Bob,Big,102.4
                """.replace("\n", "\r\n"), result.getResponse().getContentAsString());
    }
}
