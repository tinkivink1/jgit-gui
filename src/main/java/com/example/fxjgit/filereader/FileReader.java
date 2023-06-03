package com.example.fxjgit.filereader;

import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public static List<String> read(File file) throws IOException {
        String extension = getFileExtension(file.getName());
        List<String> output = new ArrayList<String>();
        switch (extension){
            case "docx":
                output = readDocx(file);
                break;
            case "pptx":
                output = readPptx(file);
                break;
            case "gif":
                output = readGif(file);
                break;
            case "xml":
                output = readXml(file);
                break;
            default:
                output = readLinesFromFile(file);
                break;
        }

        return output;
    }

    private static String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    private static List<String> readDocx(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        XWPFDocument docx = new XWPFDocument(fis);
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        String text = extractor.getText();
        extractor.close();
        docx.close();
        fis.close();

        String[] lines = text.split("\n");
        List<String> docxLines = new ArrayList<>();
        for (String line : lines) {
            docxLines.add(line);
        }

        return docxLines;
    }

    private static List<String> readPptx(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        XMLSlideShow pptx = new XMLSlideShow(fis);

        List<String> pptxLines = new ArrayList<>();
        for (XSLFSlide slide : pptx.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    pptxLines.add(textShape.getText());
                }
            }
        }

        pptx.close();
        fis.close();

        return pptxLines;
    }

    private static List<String> readGif(File file) {
        // Logic to read GIF files
        System.out.println("Reading GIF file: " + file.getName());
        // Implement the logic to read the GIF file here

        // Sample lines for demonstration
        List<String> gifLines = new ArrayList<>();
        gifLines.add("This is line 1");
        gifLines.add("This is line 2");
        gifLines.add("This is line 3");

        return gifLines;
    }

    private static List<String> readXml(File file) {
        // Logic to read XML files
        System.out.println("Reading XML file: " + file.getName());

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(file);
            document.getDocumentElement().normalize();

            // Get the root element of the XML file
            Element rootElement = document.getDocumentElement();
            // Traverse and process the XML structure
            List<String> xmlLines = traverseXmlStructure(rootElement, 0);
            return xmlLines;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static List<String> traverseXmlStructure(Element element, int depth) {
        List<String> xmlLines = new ArrayList<>();
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        String line = indent.toString() + "Element: " + element.getTagName();
        xmlLines.add(line);

        // Process child elements recursively
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                List<String> childLines = traverseXmlStructure(childElement, depth + 1);
                xmlLines.addAll(childLines);
            }
        }

        return xmlLines;
    }

    public static List<String> readLinesFromFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        try (java.io.FileReader fileReader = new java.io.FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
}
