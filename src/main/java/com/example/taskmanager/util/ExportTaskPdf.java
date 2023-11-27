package com.example.taskmanager.util;

import com.example.taskmanager.entity.Task;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.util.List;

public class ExportTaskPdf {
    private final PDDocument document;
    private final List<Task> listTasks;

    public ExportTaskPdf(List<Task> listTasks) {
        this.listTasks = listTasks;
        document = new PDDocument();
    }

    private void writeHeaderLine(PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(PDType0Font.load(document, getClass().getResourceAsStream("/fonts/times.ttf")), 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(10, 750);
        contentStream.showText("Raport TaskManager");
        contentStream.endText();
    }

    private void writeDataLines(PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(PDType0Font.load(document, getClass().getResourceAsStream("/fonts/times.ttf")), 12);

        float xStart = 20; // Początkowa współrzędna x
        float yStart = 550; // Początkowa współrzędna y

        PDPage currentPage = null;

        for (Task task : listTasks) {
            if (yStart < 50) {
                currentPage = new PDPage();
                document.addPage(currentPage);

                try (PDPageContentStream newContentStream = new PDPageContentStream(document, currentPage)) {
                    yStart = 700;
                    writeHeaderLine(newContentStream);
                }
            }

            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart);
            contentStream.showText("ID zadania: " + task.getId());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart - 20);
            contentStream.showText("ID właściciela zadania: " + task.getUser().getId());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart - 40);
            contentStream.showText("Właściciel zadania: " + task.getUser().getEmail());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart - 60);
            contentStream.showText("Nazwa zadania: " + task.getTaskName());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart - 80);
            contentStream.showText("Opis zadania: " + task.getTaskDescription());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(xStart, yStart - 100);
            contentStream.showText("Priorytet zadania: " + task.getPriority());
            contentStream.endText();

            yStart -= 120;
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            writeHeaderLine(contentStream);
            writeDataLines(contentStream);
        }

        ServletOutputStream outputStream = response.getOutputStream();
        document.save(outputStream);
        document.close();
        outputStream.close();
    }
}
