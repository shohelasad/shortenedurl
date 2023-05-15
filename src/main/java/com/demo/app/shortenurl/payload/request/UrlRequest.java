package com.demo.app.shortenurl.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UrlRequest {
	@NotBlank
	private String originalUrl;

	public UrlRequest(String originalUrl) {
		this.originalUrl = originalUrl;
	}
}
