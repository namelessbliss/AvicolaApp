package namelessbliss.tunquisolutions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import namelessbliss.tunquisolutions.Modelo.Boleta;

public class TemplatePDF {
    private Context context;
    private File folder;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;

    private Font fTitle = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    private Font fsubTitle = new Font(Font.FontFamily.COURIER, 18, Font.BOLD, BaseColor.WHITE);
    private Font fsubTitleBlack = new Font(Font.FontFamily.COURIER, 18, Font.BOLD, BaseColor.BLACK);
    private Font fText = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
    private Font slogan = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
    private Font fHighText = new Font(Font.FontFamily.COURIER, 16, Font.BOLD, BaseColor.RED);

    public TemplatePDF(Context context) {
        this.context = context;
    }

    public void createFile(String name) {
        folder = new File(Environment.getExternalStorageDirectory().toString(), "TunquiSolutionsPDF");

        if (!folder.exists())
            folder.mkdirs();

        pdfFile = new File(folder, name + ".pdf");
    }

    public void openDocument(String name) {
        createFile(name);
        try {
            document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile.getAbsolutePath()));
            document.open();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("openDocument", e.toString());
        }
    }

    public void closeDocument() {
        document.close();
    }

    public void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String address, String phone, String subtitle, String date) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph("Dirección : " + address, fsubTitleBlack));
            addChildP(new Paragraph("Pedidos al : " + phone, fsubTitleBlack));
            addChildP(new Paragraph("Fecha : " + date, fHighText));
            addChildP(new Paragraph("Cliente : " + subtitle, fsubTitleBlack));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTitles", e.toString());
        }
    }

    public void addImgName(Image imageFile) {
        try {
            /*Image image = Image.getInstance(folder.getAbsolutePath() + File.separator + imageFile);
            image.setSpacingBefore(5);
            image.setSpacingAfter(5);*/
            imageFile.scaleToFit(180, 180);
            imageFile.setAlignment(Element.ALIGN_LEFT);
            document.add(imageFile);
        } catch (Exception e) {
            Log.e("addImgName ", e.toString());
        }
    }

    public void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addParagraph ", e.toString());
        }
    }

    public void addSlogan() {
        try {
            paragraph = new Paragraph("La mejor calidad \n al mejor precio", slogan);
            paragraph.setSpacingAfter(5);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addSlogan ", e.toString());
        }
    }

    public void createTable(String[] header, ArrayList<String[]> productos) {
        float subtotal = 0;
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;
            int index = 0;
            while (index < header.length) {
                pdfPCell = new PdfPCell(new Phrase(header[index++], fsubTitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(new BaseColor(10, 36, 58));
                pdfPTable.addCell(pdfPCell);
            }

            // llena las celdas del detalle de los productos
            for (int indexR = 0; indexR < productos.size(); indexR++) {
                String[] row = productos.get(indexR);
                for (int indexC = 0; indexC < row.length; indexC++) {
                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    pdfPTable.addCell(pdfPCell);
                }
            }
            for (int indexSub = 0; indexSub < header.length - 2; indexSub++) {
                pdfPCell = new PdfPCell(new Phrase(""));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(40);
                pdfPCell.setBorderColor(BaseColor.WHITE);
                pdfPTable.addCell(pdfPCell);
            }
            for (String[] datoArray : productos) {
                subtotal += Float.parseFloat(datoArray[4]);
            }
            pdfPCell = new PdfPCell(new Phrase("Sub total: "));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(40);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase(String.valueOf(subtotal)));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(40);
            pdfPTable.addCell(pdfPCell);

            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("createTable ", e.toString());
        }
    }

    public void viewPDF() {
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        System.out.println(pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
