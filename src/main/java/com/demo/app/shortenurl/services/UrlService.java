package com.demo.app.shortenurl.services;

import com.demo.app.shortenurl.models.UrlEntity;
import com.demo.app.shortenurl.payload.response.UrlResponse;
import com.demo.app.shortenurl.repository.UrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    private String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private SecureRandom secureRandom = new SecureRandom();


    private UrlRepository urlRepository;
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponse save(UrlEntity urlEntity) {
        List<UrlEntity> entities = urlRepository.findByUsernameAndOriginalUrl(urlEntity.getUsername(), urlEntity.getOriginalUrl());
        if(!entities.isEmpty()) {
           urlEntity = entities.get(0);
           urlEntity.setShortCount(urlEntity.getShortCount() + 1);
        } else {
           urlEntity.setShortCount(1l);
           urlEntity.setAccessCount(0l);
        }
        return convertToUrlResponse(urlRepository.save(urlEntity));
    }

    public UrlResponse updateAccessCount(UrlEntity urlEntity) {
        urlEntity.setAccessCount(urlEntity.getAccessCount() + 1);
        return convertToUrlResponse(urlRepository.save(urlEntity));
    }

    public Optional<UrlEntity> findByShortUrl(String shortenUrl) {
        return urlRepository.findByShortenUrl(shortenUrl);
    }

    public List<UrlResponse> findAllByOriginalUrl(String originalUrl) {
        return convertToUrlResponse(urlRepository.findAllByOriginalUrl(originalUrl));
    }

    public List<UrlResponse> findByUsernameAndOriginalUrl(String username, String originalUrl) {
        return convertToUrlResponse(urlRepository.findByUsernameAndOriginalUrl(username, originalUrl));
    }

    public String generateShortUrl(String originalUrl) {
        int length = 8;
        StringBuilder sb = new StringBuilder();
        Random random = new Random(secureRandom.nextLong());
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        if(!urlRepository.findByShortenUrl(sb.toString()).isPresent()) {
            return sb.toString();
        }

        return generateShortUrl(originalUrl);
    }

    private UrlResponse convertToUrlResponse(UrlEntity urlEntity) {
        UrlResponse response = new UrlResponse();
        response.setOriginalUrl(urlEntity.getOriginalUrl());
        response.setShortUrl(urlEntity.getShortenUrl());
        response.setShortenCount(urlEntity.getShortCount());
        response.setAccessCount(urlEntity.getAccessCount());
        response.setUsername(urlEntity.getUsername());
        return response;
    }

    private List<UrlResponse> convertToUrlResponse(List<UrlEntity> urlEntities) {
        List<UrlResponse> responses = new ArrayList<>();
        urlEntities.forEach(urlEntity -> responses.add(convertToUrlResponse(urlEntity)));

        return responses;
    }
}
