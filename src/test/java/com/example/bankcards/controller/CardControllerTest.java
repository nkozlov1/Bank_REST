package com.example.bankcards.controller;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.bankcards.exception.CardNotFoundException;

@WebMvcTest(CardController.class)
@ContextConfiguration(classes = {CardController.class})
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
public class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Test
    @DisplayName("POST /card/save - success")
    @WithMockUser(roles = "ADMIN")
    void saveCard_Success() throws Exception {
        CardDto card = new CardDto(1L, "1234-5678-9012-3456", "John Doe", LocalDate.now().plusYears(2), CardStatus.Active, BigDecimal.valueOf(1000));
        when(cardService.save(anyLong())).thenReturn(card);
        String json = "{\"holderId\":1}";
        mockMvc.perform(post("/card/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()));
    }

    @Test
    @DisplayName("POST /card/save - invalid input")
    @WithMockUser(roles = "ADMIN")
    void saveCard_InvalidInput() throws Exception {
        String json = "{\"}";
        mockMvc.perform(post("/card/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /card/deleteById - success")
    @WithMockUser(roles = "ADMIN")
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/card/deleteById").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /card/deleteById - not found")
    @WithMockUser(roles = "ADMIN")
    void deleteById_NotFound() throws Exception {
        doThrow(new CardNotFoundException("Card not found")).when(cardService).deleteById(999L);
        mockMvc.perform(delete("/card/deleteById").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /card/deleteAll - success")
    @WithMockUser(roles = "ADMIN")
    void deleteAll_Success() throws Exception {
        mockMvc.perform(delete("/card/deleteAll"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /card/deleteAll - forbidden for non-admin")
    @WithMockUser(roles = "USER")
    void deleteAll_Forbidden() throws Exception {
        mockMvc.perform(delete("/card/deleteAll"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /card/update - success")
    @WithMockUser(roles = "ADMIN")
    void updateCard_Success() throws Exception {
        CardDto card = new CardDto(1L, "1234-5678-9012-3456", "John Doe", LocalDate.now().plusYears(2), CardStatus.Active, BigDecimal.valueOf(1000));
        when(cardService.update(anyLong(), any(), any(), any(), any())).thenReturn(card);
        String json = "{\"id\":1}";
        mockMvc.perform(put("/card/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()));
    }


    @Test
    @DisplayName("GET /card/getById - success")
    @WithMockUser(roles = "ADMIN")
    void getById_Success() throws Exception {
        CardDto card = new CardDto(1L, "1234-5678-9012-3456", "John Doe", LocalDate.now().plusYears(2), CardStatus.Active, BigDecimal.valueOf(1000));
        when(cardService.getById(1L)).thenReturn(card);
        mockMvc.perform(get("/card/getById").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.getId()));
    }

    @Test
    @DisplayName("GET /card/getById - not found")
    @WithMockUser(roles = "ADMIN")
    void getById_NotFound() throws Exception {
        when(cardService.getById(999L)).thenThrow(new CardNotFoundException("Card not found"));
        mockMvc.perform(get("/card/getById").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /card/getAll - success")
    @WithMockUser(roles = "ADMIN")
    void getAll_Success() throws Exception {
        CardDto card = new CardDto(1L, "1234-5678-9012-3456", "John Doe", LocalDate.now().plusYears(2), CardStatus.Active, BigDecimal.valueOf(1000));
        when(cardService.getAll(any(CardFilter.class), any())).thenReturn(List.of(card));
        mockMvc.perform(get("/card/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(card.getId()));
    }

    @Test
    @DisplayName("GET /card/getAll - forbidden for non-admin")
    @WithMockUser(roles = "USER")
    void getAll_Forbidden() throws Exception {
        mockMvc.perform(get("/card/getAll"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /card/getAllHolderCards - success")
    @WithMockUser(username = "user1")
    void getAllHolderCards_Success() throws Exception {
        CardDto card = new CardDto(1L, "1234-5678-9012-3456", "user1", LocalDate.now().plusYears(2), CardStatus.Active, BigDecimal.valueOf(1000));
        when(cardService.getCardsByHolderName(anyString(), any(CardFilter.class), any())).thenReturn(List.of(card));
        mockMvc.perform(get("/card/getAllHolderCards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].holderName").value("user1"));
    }

    @Test
    @DisplayName("GET /card/getAllHolderCards - unauthorized")
    void getAllHolderCards_Unauthorized() throws Exception {
        mockMvc.perform(get("/card/getAllHolderCards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /card/transfer - success")
    @WithMockUser(username = "user1")
    void transferBetweenCards_Success() throws Exception {
        String json = "{\"fromCardId\":1,\"toCardId\":2,\"amount\":100}";
        mockMvc.perform(post("/card/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /card/blockCard - success")
    @WithMockUser(username = "user1")
    void blockCard_Success() throws Exception {
        mockMvc.perform(patch("/card/blockCard").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /card/blockCard - not found")
    @WithMockUser(username = "user1")
    void blockCard_NotFound() throws Exception {
        doThrow(new CardNotFoundException("Card not found")).when(cardService).blockCard(anyString(), eq(999L));
        mockMvc.perform(patch("/card/blockCard").param("id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /card/getBalance - success")
    @WithMockUser(username = "user1")
    void getBalance_Success() throws Exception {
        when(cardService.getBalance(anyString(), eq(1L))).thenReturn(BigDecimal.valueOf(1000));
        mockMvc.perform(patch("/card/getBalance").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /card/getBalance - not found")
    @WithMockUser(username = "user1")
    void getBalance_NotFound() throws Exception {
        doThrow(new CardNotFoundException("Card not found")).when(cardService).getBalance(anyString(), eq(999L));
        mockMvc.perform(patch("/card/getBalance").param("id", "999"))
                .andExpect(status().isNotFound());
    }
}
