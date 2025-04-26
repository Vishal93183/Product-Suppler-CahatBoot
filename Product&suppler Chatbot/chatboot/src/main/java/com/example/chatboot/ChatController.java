package com.example.chatboot;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;
    private final MyPagePdfDocumentReader pdfReader;
    private final PdfQuestionService pdfQuestionService;

    public ChatController(OllamaChatModel chatModel,
                          MyPagePdfDocumentReader pdfReader,
                          PdfQuestionService pdfQuestionService) {
        this.chatModel = chatModel;
        this.pdfReader = pdfReader;
        this.pdfQuestionService = pdfQuestionService;
    }

    @PostMapping("/ai/generate")
    public Map<String, String> generate(@RequestBody ChatRequest request) {
        String message = request.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return Map.of("generation", "Please enter a valid question.");
        }

        try {
            List<Document> pdfDocs = pdfReader.getDocsFromPdf();

            if (!pdfQuestionService.isQuestionRelatedToPdf(message, pdfDocs)) {
                return Map.of("generation", "I'm sorry, I can only answer questions related to the PDF content.");
            }

            String prompt = pdfQuestionService.buildPromptWithContext(message, pdfDocs);
            String response = chatModel.call(prompt);
            return Map.of("generation", response);

        } catch (Exception e) {
            return Map.of("generation", "Error: " + e.getMessage());
        }
    }
}
