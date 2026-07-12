package com.dealership.inventory.controller;

import com.dealership.inventory.dto.request.PurchaseRequest;
import com.dealership.inventory.dto.response.PurchaseResponse;
import com.dealership.inventory.model.PurchaseRecord;
import com.dealership.inventory.model.User;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.service.InvoiceService;
import com.dealership.inventory.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller managing transaction orders, purchase records, and customer receipts.
 */
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    @Autowired
    public PurchaseController(PurchaseService purchaseService,
                              InvoiceService invoiceService,
                              UserRepository userRepository) {
        this.purchaseService = purchaseService;
        this.invoiceService = invoiceService;
        this.userRepository = userRepository;
    }

    /**
     * Executes a vehicle purchase order. Stock quantities are decremented atomically.
     *
     * @param request purchase request containing vehicle identifier and unit quantity
     * @param userDetails credentials of the authenticated customer initiating the order
     * @return 201 Created containing purchase confirmation details
     */
    @PostMapping
    public ResponseEntity<PurchaseResponse> purchaseVehicle(
            @Valid @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PurchaseResponse response = purchaseService.purchaseVehicle(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Fetches the historical purchase receipts associated with the authenticated customer.
     *
     * @param userDetails credentials of the logged-in customer
     * @return 200 OK list of transaction records
     */
    @GetMapping("/history")
    public ResponseEntity<List<PurchaseResponse>> getPurchaseHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PurchaseResponse> response = purchaseService.getPurchaseHistory(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all transactional order audits recorded in the system.
     *
     * @return 200 OK list of system transaction records
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PurchaseResponse>> getAllPurchases() {
        List<PurchaseResponse> response = purchaseService.getAllPurchases();
        return ResponseEntity.ok(response);
    }

    /**
     * Generates and downloads the PDF invoice attachment for a specific transaction record.
     * Enforces ownership checking: customers can download their own invoice, admins can access any record.
     *
     * @param id purchase transaction identifier
     * @param userDetails credentials of the user requesting the invoice
     * @return 200 OK raw PDF binary attachment stream
     */
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        PurchaseRecord record = purchaseService.getPurchaseRecordById(id);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new AccessDeniedException("User session not found"));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !record.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to access this invoice");
        }

        byte[] pdfBytes = invoiceService.generateInvoicePdf(record);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
