package com.demo.app.shortenurl.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponse {
    private String originalUrl;

    private String shortUrl;

    private Long shortenCount;

    private Long accessCount;

    private String username;
}
