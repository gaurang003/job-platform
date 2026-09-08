package AI.Job.Application.Platform.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class PdfTextExtractorService {

    public String extractText(Path pdfPath) throws IOException {

        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {

            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            pdfTextStripper.setSortByPosition(true);

            return pdfTextStripper.getText(document);
        }
    }
}