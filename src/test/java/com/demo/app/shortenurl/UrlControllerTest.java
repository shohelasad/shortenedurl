package com.demo.app.shortenurl;

import com.demo.app.shortenurl.controllers.UrlController;
import com.demo.app.shortenurl.models.UrlEntity;
import com.demo.app.shortenurl.payload.request.UrlRequest;
import com.demo.app.shortenurl.payload.response.UrlResponse;
import com.demo.app.shortenurl.security.jwt.JwtTokenUtils;
import com.demo.app.shortenurl.security.jwt.UserDetailsImpl;
import com.demo.app.shortenurl.services.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private UrlController urlController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    public void testCreateShortenUrl_Success() throws Exception {
        String originalUrl = "https://example.com";
        String shortUrl = "abc123";
        UrlRequest urlRequest = new UrlRequest(originalUrl);
        UrlEntity urlEntity = new UrlEntity(originalUrl, shortUrl, "user1");
        UrlResponse expectedResponse = new UrlResponse(originalUrl, shortUrl, 1L, 0L, "user1");

        when(urlService.generateShortUrl(originalUrl)).thenReturn(shortUrl);
        when(urlService.save(any(UrlEntity.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(urlRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.shortUrl").value(shortUrl))
                .andExpect(jsonPath("$.shortenCount").value(1))
                .andExpect(jsonPath("$.accessCount").value(0))
                .andExpect(jsonPath("$.username").value("user1"));

        verify(urlService, times(1)).generateShortUrl(originalUrl);
        verify(urlService, times(1)).save(any(UrlEntity.class));
    }

    @Test
    void testRedirectToOriginalUrl_UrlFound_ShouldReturnRedirectResponse() {
        String shortenUrl = "abc123";
        String originalUrl = "https://example.com";
        UrlEntity urlEntity = new UrlEntity(originalUrl, shortenUrl, "user1");
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setLocation(URI.create(originalUrl));
        ResponseEntity<Void> expectedResponse = new ResponseEntity<>(expectedHeaders, HttpStatus.MOVED_PERMANENTLY);

        when(urlService.findByShortUrl(shortenUrl)).thenReturn(Optional.of(urlEntity));

        ResponseEntity<Void> actualResponse = urlController.redirectToOriginalUrl(shortenUrl);

        Assertions.assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        Assertions.assertEquals(expectedResponse.getHeaders(), actualResponse.getHeaders());
        verify(urlService, times(1)).findByShortUrl(shortenUrl);
        verify(urlService, times(1)).updateAccessCount(urlEntity);
    }

    @Test
    void testRedirectToOriginalUrl_UrlNotFound_ShouldReturnNotFoundResponse() {
        String shortenUrl = "abc123";
        ResponseEntity<Void> expectedResponse = ResponseEntity.notFound().build();

        when(urlService.findByShortUrl(shortenUrl)).thenReturn(Optional.empty());

        ResponseEntity<Void> actualResponse = urlController.redirectToOriginalUrl(shortenUrl);

        Assertions.assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        verify(urlService, times(1)).findByShortUrl(shortenUrl);
        verify(urlService, never()).updateAccessCount(any());
    }

    private String asJsonString(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
