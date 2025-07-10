package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardFilter;
import com.example.bankcards.util.requests.SaveCardRequest;
import com.example.bankcards.util.requests.TransferBetweenCardsRequest;
import com.example.bankcards.util.requests.UpdateCardRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/card")
@Tag(name = "Card", description = "Card management endpoints")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Create new card", description = "Creates a new card. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card created", content = @Content(schema = @Schema(implementation = CardDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> save(@Valid @RequestBody SaveCardRequest request) {
        System.out.println(request);
        return ResponseEntity.ok(cardService.save(request.getHolderId()));
    }

    @Operation(summary = "Delete card by ID", description = "Deletes a card by ID. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @DeleteMapping("/deleteById")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Card ID", required = true, example = "100") @RequestParam Long id) {
        cardService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete all cards", description = "Deletes all cards. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All cards deleted", content = @Content)
    })
    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteAll() {
        cardService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update card", description = "Updates card data. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card updated", content = @Content(schema = @Schema(implementation = CardDto.class))),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CardDto> update(@Valid @RequestBody UpdateCardRequest request) {
        return ResponseEntity.ok(cardService.update(request.getId(), request.getNewHolderId(), request.getNewExpirationDate(), request.getNewStatus(), request.getNewBalance()));
    }

    @Operation(summary = "Get card by ID", description = "Returns card by ID. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card found", content = @Content(schema = @Schema(implementation = CardDto.class))),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CardDto> getById(
            @Parameter(description = "Card ID", required = true, example = "100") @RequestParam Long id) {
        return ResponseEntity.ok(cardService.getById(id));
    }

    @Operation(summary = "Get all cards", description = "Returns all cards with optional filtering. Only for ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of cards", content = @Content(schema = @Schema(implementation = CardDto.class)))
    })
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardDto>> getAll(
            @Parameter(description = "Card filter") @ModelAttribute CardFilter filter,
            @Parameter(description = "Page offset", example = "0") @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @Parameter(description = "Page limit", example = "20") @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return ResponseEntity.ok(cardService.getAll(filter, PageRequest.of(offset, limit)));
    }

    @Operation(summary = "Get all cards of current user", description = "Returns all cards of the authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of cards", content = @Content(schema = @Schema(implementation = CardDto.class)))
    })
    @GetMapping("/getAllHolderCards")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CardDto>> getAllHolderCards(
            @Parameter(description = "Card filter") @ModelAttribute CardFilter filter,
            @Parameter(description = "Page offset", example = "0") @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @Parameter(description = "Page limit", example = "20") @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(cardService.getCardsByHolderName(username, filter, PageRequest.of(offset, limit)));
    }

    @Operation(summary = "Transfer between cards", description = "Transfers money between cards. Only for authenticated users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer successful", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> transferBetweenCards(@Valid @RequestBody TransferBetweenCardsRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        cardService.transferBetweenCards(username, request.getFromCardId(), request.getToCardId(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Block card", description = "Blocks a card. Only for authenticated users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card blocked", content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @PatchMapping("/blockCard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> blockCard(
            @Parameter(description = "Card ID", required = true, example = "100") @RequestParam Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        cardService.blockCard(username, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get card balance", description = "Returns card balance. Only for authenticated users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance returned", content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @PatchMapping("/getBalance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> getBalance(
            @Parameter(description = "Card ID", required = true, example = "100") @RequestParam Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        cardService.getBalance(username, id);
        return ResponseEntity.ok().build();
    }
}
