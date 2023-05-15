package com.demo.app.shortenurl;

import com.demo.app.shortenurl.models.UrlEntity;
import com.demo.app.shortenurl.payload.response.UrlResponse;
import com.demo.app.shortenurl.repository.UrlRepository;
import com.demo.app.shortenurl.services.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    private UrlService urlService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        urlService = new UrlService(urlRepository);
    }

    @Test
    public void testFindByShortUrl_ExistingUrlEntity_ShouldReturnOptionalUrlEntity() {
        String shortenUrl = "shorturl1";
        UrlEntity existingUrlEntity = new UrlEntity("https://example.com", shortenUrl, "user1");

        when(urlRepository.findByShortenUrl(shortenUrl)).thenReturn(Optional.of(existingUrlEntity));
        Optional<UrlEntity> result = urlService.findByShortUrl(shortenUrl);

        assertTrue(result.isPresent());
        assertEquals(existingUrlEntity, result.get());
        verify(urlRepository, times(1)).findByShortenUrl(shortenUrl);
    }

    @Test
    public void testFindByShortUrl_NonExistingUrlEntity_ShouldReturnEmptyOptional() {
        String shortenUrl = "shorturl2";
        when(urlRepository.findByShortenUrl(shortenUrl)).thenReturn(Optional.empty());
        Optional<UrlEntity> result = urlService.findByShortUrl(shortenUrl);

        assertTrue(result.isEmpty());
        verify(urlRepository, times(1)).findByShortenUrl(shortenUrl);
    }

    @Test
    public void testFindAllByOriginalUrl_ExistingUrlEntities_ShouldReturnListUrlResponse() {
        String originalUrl = "https://example.com";
        UrlEntity urlEntity1 = new UrlEntity(originalUrl, "shorturl1", "user1");
        UrlEntity urlEntity2 = new UrlEntity(originalUrl, "shorturl2", "user2");
        List<UrlEntity> urlEntities = new ArrayList<>();
        urlEntities.add(urlEntity1);
        urlEntities.add(urlEntity2);

        when(urlRepository.findAllByOriginalUrl(originalUrl)).thenReturn(urlEntities);

        List<UrlResponse> result = urlService.findAllByOriginalUrl(originalUrl);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(urlRepository, times(1)).findAllByOriginalUrl(originalUrl);
    }

    @Test
    public void testFindAllByOriginalUrl_NoUrlEntities_ShouldReturnEmptyList() {
        String originalUrl = "https://example.com";
        List<UrlEntity> urlEntities = new ArrayList<>();

        when(urlRepository.findAllByOriginalUrl(originalUrl)).thenReturn(urlEntities);
        List<UrlResponse> result = urlService.findAllByOriginalUrl(originalUrl);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(urlRepository, times(1)).findAllByOriginalUrl(originalUrl);
    }

    @Test
    public void testGenerateShortUrl_NoDuplicateShortUrl_ShouldReturnShortUrl() {
        String originalUrl = "https://example.com";
        when(urlRepository.findByShortenUrl(anyString())).thenReturn(Optional.empty());

        String result = urlService.generateShortUrl(originalUrl);

        assertNotNull(result);
        assertEquals(8, result.length());
        verify(urlRepository, atLeastOnce()).findByShortenUrl(anyString());
    }
}
