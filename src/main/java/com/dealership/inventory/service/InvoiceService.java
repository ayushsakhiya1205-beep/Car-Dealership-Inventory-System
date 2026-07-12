package com.dealership.inventory.service;

import com.dealership.inventory.exception.ResourceNotFoundException;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.User;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.repository.VehicleRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Service class responsible for generating beautifully formatted PDF invoices
 * for vehicle purchases using the OpenPDF layout library.
 */
@Service
public class InvoiceService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public InvoiceService(UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Programmatically constructs a styled A4 PDF invoice document containing
     * transaction metadata, client details, vehicle models, quantities, and totals.
     *
     * @param record target purchase transaction order
     * @return raw binary PDF byte array
     */
    public byte[] generateInvoicePdf(PurchaseRecord record) {
        User user = userRepository.findById(record.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + record.getUserId()));

        Vehicle vehicle = vehicleRepository.findById(record.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + record.getVehicleId()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Font styles setup
            Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(124, 58, 237)); // Violet 600
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font textBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, Color.GRAY);

            // Invoice sections delegation (SRP)
            addInvoiceHeader(document, record, brandFont, titleFont, textFont);
            addBillingInfo(document, user, sectionTitleFont, textFont);

            double totalAmt = record.getPurchasePrice() * record.getQuantity();
            addItemizedTable(document, vehicle, record, totalAmt, headerFont, textFont, textBoldFont);
            addSummaryTotals(document, totalAmt, textFont, textBoldFont);
            addInvoiceFooter(document, sectionTitleFont, footerFont);

            document.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void addInvoiceHeader(Document document, PurchaseRecord record, Font brandFont, Font titleFont, Font textFont) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        // Logo cell
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.addElement(new Paragraph("AutoHaven", brandFont));
        logoCell.addElement(new Paragraph("Premium Dealership Networks", FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, Color.GRAY)));
        headerTable.addCell(logoCell);

        // Metadata cell
        PdfPCell metaCell = new PdfPCell();
        metaCell.setBorder(Rectangle.NO_BORDER);
        metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph invTitle = new Paragraph("INVOICE", titleFont);
        invTitle.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(invTitle);

        String formattedDate = record.getPurchasedAt() != null
                ? record.getPurchasedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "";
        Paragraph metaDetails = new Paragraph(
                "Invoice #: INV-" + record.getId().substring(Math.max(0, record.getId().length() - 8)).toUpperCase() + "\n" +
                "Date: " + formattedDate + "\n" +
                "Status: PAID", textFont);
        metaDetails.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(metaDetails);
        headerTable.addCell(metaCell);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);
    }

    private void addBillingInfo(Document document, User user, Font sectionTitleFont, Font textFont) throws DocumentException {
        Paragraph billTitle = new Paragraph("BILL TO:", sectionTitleFont);
        document.add(billTitle);
        Paragraph billDetails = new Paragraph(
                "Customer Name: " + user.getUsername() + "\n" +
                "Customer ID: " + user.getId(), textFont);
        billDetails.setIndentationLeft(10);
        document.add(billDetails);
        document.add(Chunk.NEWLINE);
    }

    private void addItemizedTable(Document document, Vehicle vehicle, PurchaseRecord record, double totalAmt,
                                  Font headerFont, Font textFont, Font textBoldFont) throws DocumentException {
        PdfPTable itemTable = new PdfPTable(5);
        itemTable.setWidthPercentage(100);
        itemTable.setWidths(new float[]{40, 15, 15, 10, 20});

        // Table headers
        String[] headers = {"Vehicle Description", "Year", "Unit Price", "Qty", "Total Amount"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(15, 23, 42)); // Slate 900
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemTable.addCell(cell);
        }

        // Item Row
        String vehicleDesc = vehicle.getMake() + " " + vehicle.getModel();
        PdfPCell descCell = new PdfPCell(new Phrase(vehicleDesc, textFont));
        descCell.setPadding(8);
        itemTable.addCell(descCell);

        PdfPCell yearCell = new PdfPCell(new Phrase(String.valueOf(vehicle.getYear()), textFont));
        yearCell.setPadding(8);
        yearCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(yearCell);

        PdfPCell priceCell = new PdfPCell(new Phrase("$" + String.format("%,.2f", record.getPurchasePrice()), textFont));
        priceCell.setPadding(8);
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(priceCell);

        PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(record.getQuantity()), textFont));
        qtyCell.setPadding(8);
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(qtyCell);

        PdfPCell totalCell = new PdfPCell(new Phrase("$" + String.format("%,.2f", totalAmt), textBoldFont));
        totalCell.setPadding(8);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(totalCell);

        document.add(itemTable);
        document.add(Chunk.NEWLINE);
    }

    private void addSummaryTotals(Document document, double totalAmt, Font textFont, Font textBoldFont) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(40);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setWidths(new float[]{50, 50});

        PdfPCell subTotalLabel = new PdfPCell(new Phrase("Subtotal:", textFont));
        subTotalLabel.setBorder(Rectangle.NO_BORDER);
        summaryTable.addCell(subTotalLabel);

        PdfPCell subTotalVal = new PdfPCell(new Phrase("$" + String.format("%,.2f", totalAmt), textFont));
        subTotalVal.setBorder(Rectangle.NO_BORDER);
        subTotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(subTotalVal);

        PdfPCell grandTotalLabel = new PdfPCell(new Phrase("Grand Total (Paid):", textBoldFont));
        grandTotalLabel.setBorder(Rectangle.NO_BORDER);
        summaryTable.addCell(grandTotalLabel);

        PdfPCell grandTotalVal = new PdfPCell(new Phrase("$" + String.format("%,.2f", totalAmt), textBoldFont));
        grandTotalVal.setBorder(Rectangle.NO_BORDER);
        grandTotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(grandTotalVal);

        document.add(summaryTable);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
    }

    private void addInvoiceFooter(Document document, Font sectionTitleFont, Font footerFont) throws DocumentException {
        Paragraph greeting = new Paragraph("Thank you for your business! Drive safely.", sectionTitleFont);
        greeting.setAlignment(Element.ALIGN_CENTER);
        document.add(greeting);

        Paragraph disclaimers = new Paragraph("This is a system generated invoice and requires no physical signature.", footerFont);
        disclaimers.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimers);
    }
}
