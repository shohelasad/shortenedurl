package com.demo.app.shortenurl.controllers;

import com.demo.app.shortenurl.models.UrlEntity;
import com.demo.app.shortenurl.payload.request.UrlRequest;
import com.demo.app.shortenurl.payload.response.UrlResponse;
import com.demo.app.shortenurl.security.jwt.JwtTokenUtils;
import com.demo.app.shortenurl.services.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

	private JwtTokenUtils jwtTokenUtils;

	private UrlService urlService;

	private UserDetailsService userDetailsService;

	public UrlController(JwtTokenUtils jwtTokenUtils, UrlService urlService, UserDetailsService userDetailsService) {
		this.jwtTokenUtils = jwtTokenUtils;
		this.urlService = urlService;
		this.userDetailsService =userDetailsService;
	}

	@PostMapping
	public ResponseEntity<UrlResponse> createShortenUrl(@RequestBody UrlRequest urlRequest, @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		String username = getAuthenticateUser(authorizationHeader);
		String shortUrl = urlService.generateShortUrl(urlRequest.getOriginalUrl());
		UrlEntity urlEntity = new UrlEntity(urlRequest.getOriginalUrl(), shortUrl, username);
		return ResponseEntity.ok(urlService.save(urlEntity));
	}

	@GetMapping("/{shortenUrl}")
	public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortenUrl) {
		Optional<UrlEntity> urlOptional = urlService.findByShortUrl(shortenUrl);
		if (urlOptional.isPresent()) {
			UrlEntity entity = urlOptional.get();
			urlService.updateAccessCount(entity);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create(entity.getOriginalUrl()));
			return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/statistics")
	public List<UrlResponse> getStatistics(@RequestBody UrlRequest urlRequest, @RequestHeader(value = "Authorization") String authorizationHeader) {
		List<UrlResponse> urlResponses = new ArrayList<>();
		String username = getAuthenticateUser(authorizationHeader);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails != null) {
			if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				urlResponses = urlService.findAllByOriginalUrl(urlRequest.getOriginalUrl());
			} else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))){
				urlResponses = urlService.findByUsernameAndOriginalUrl(userDetails.getUsername(), urlRequest.getOriginalUrl());
			}
		}
		return urlResponses;
	}

	private String getAuthenticateUser(String authorizationHeader) {
		String username = "guest";
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7);
			username = jwtTokenUtils.getUserNameFromJwtToken(jwtToken);
		}
		return username;
	}
}
