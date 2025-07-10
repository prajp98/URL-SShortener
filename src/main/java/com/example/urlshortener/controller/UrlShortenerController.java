package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlAnalytics;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestParam String url) {
        return ResponseEntity.ok(service.shortenUrl(url));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String originalUrl = service.getOriginalUrl(shortCode, userAgent);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<List<UrlAnalytics>> analytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(service.getAnalytics(shortCode));
    }
}
