package com.example.pdftoaudio.demo.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.pdftoaudio.demo.services.PDFReader;
import com.example.pdftoaudio.demo.services.TextToSpeech;
import com.example.pdftoaudio.demo.services.TextToSpeechDaisy;

@RestController
@RequestMapping("/pdftoaudio")
public class PDFToSpeechController {
    private final TextToSpeechDaisy textToSpeech;
    private final PDFReader pdfReader;

    public PDFToSpeechController() {
        this.textToSpeech = new TextToSpeechDaisy();
        this.pdfReader = new PDFReader();
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertPdfToSpeech(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try {
            // Step 1: Convert MultipartFile to File
            Path tempFile = Files.createTempFile("uploaded-", ".pdf");
            multipartFile.transferTo(tempFile.toFile());

            // Step 2: Extract text from the PDF
            File pdfFile = new File(tempFile.toFile().getAbsolutePath());
            String extractedText = pdfReader.extractText(pdfFile);

            // Step 3: Convert the extracted text to speech
            File audioFile = textToSpeech.convertTextToSpeech(extractedText, "test.wav");

            // Step 4: Return the audio file as a download
            return ResponseEntity.status(HttpStatus.OK).body("File converted");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during processing");
        }
    }

}
