package com.demo.app.shortenurl.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(	name = "urls",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"originalUrl", "username"})
        })
public class UrlEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;

    private String shortenUrl;

    private Long shortCount;

    private Long accessCount;

    private String username;

    public UrlEntity(String originalUrl, String shortenUrl, String username) {
        this.originalUrl = originalUrl;
        this.shortenUrl = shortenUrl;
        this.username = username;
    }
}
