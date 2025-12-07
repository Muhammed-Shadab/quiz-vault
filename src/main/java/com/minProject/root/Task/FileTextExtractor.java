package com.minProject.root.Task;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileTextExtractor {
//
//    private final TesseractOCRConfig ocrConfig;
//
//    // Constructor injection
//    public FileTextExtractor(TesseractOCRConfig ocrConfig) {
//        this.ocrConfig = ocrConfig;
//    }

    public String extractText(InputStream stream) {
        try {
            // 1. Tika handler stores extracted text
            BodyContentHandler handler = new BodyContentHandler(-1); // -1 = unlimited size

            // 2. Metadata object holds file info (author, title, etc.)
            Metadata metadata = new Metadata();

            // 3. Auto parser detects file type automatically
            AutoDetectParser parser = new AutoDetectParser();

            // 4. ParseContext tells Tika OCR should be used
            ParseContext context = new ParseContext();
//            context.set(TesseractOCRConfig.class, ocrConfig);

            // 5. Actually parse the file
            parser.parse(stream, handler, metadata,context);

            // 6. Return extracted text
            return handler.toString();

        } catch (Exception e) {
            throw new RuntimeException("Text extraction failed: " + e.getMessage());
        }
    }
}