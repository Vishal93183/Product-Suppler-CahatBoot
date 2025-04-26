package com.example.chatboot;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdfQuestionService {

    public boolean isQuestionRelatedToPdf(String question, List<Document> pdfDocs) {
        String[] keywords = question.toLowerCase().split("\\s+");
        for (Document doc : pdfDocs) {
            String content = doc.getText().toLowerCase();
            for (String keyword : keywords) {
                if (content.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String buildPromptWithContext(String question, List<Document> pdfDocs) {
        StringBuilder context = new StringBuilder("The following is product supply data:\n\n");
        for (Document doc : pdfDocs) {
            context.append(doc.getText().trim()).append("\n\n");
        }

        context.append("Based on the above information, answer the question:\n")
               .append("Q: ").append(question).append("\nA:");

        return context.toString();
    }
}
