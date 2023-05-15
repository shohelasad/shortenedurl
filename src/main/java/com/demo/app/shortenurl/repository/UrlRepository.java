package com.demo.app.shortenurl.repository;

import com.demo.app.shortenurl.models.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
  Optional<UrlEntity> findByShortenUrl(String shortenUrl);

  List<UrlEntity> findAllByOriginalUrl(String originalUrl);

  List<UrlEntity> findByUsernameAndOriginalUrl(String username, String originalUrl);
}
