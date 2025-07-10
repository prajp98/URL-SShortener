package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlAnalytics;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlAnalyticsRepository;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository mappingRepo;
    private final UrlAnalyticsRepository analyticsRepo;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public UrlShortenerService(UrlMappingRepository m, UrlAnalyticsRepository a, StringRedisTemplate r) {
        this.mappingRepo = m;
        this.analyticsRepo = a;
        this.redisTemplate = r;
    }

    public String shortenUrl(String originalUrl) {
        String shortCode = Base64.getUrlEncoder()
                .encodeToString(UUID.randomUUID().toString().getBytes())
                .substring(0, 6);

        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(shortCode);
        mapping.setOriginalUrl(originalUrl);
        mappingRepo.save(mapping);
        redisTemplate.opsForValue().set(shortCode, originalUrl);
        return shortCode;
    }

    public String getOriginalUrl(String shortCode, String userAgent) {
        String url = redisTemplate.opsForValue().get(shortCode);
        if (url == null) {
            UrlMapping mapping = mappingRepo.findById(shortCode)
                    .orElseThrow(() -> new RuntimeException("Shortcode not found"));
            url = mapping.getOriginalUrl();
            redisTemplate.opsForValue().set(shortCode, url);
        }

        UrlAnalytics a = new UrlAnalytics();
        a.setShortCode(shortCode);
        a.setUserAgent(userAgent);
        a.setTimestamp(LocalDateTime.now());
        analyticsRepo.save(a);

        return url;
    }

    public List<UrlAnalytics> getAnalytics(String shortCode) {
        return analyticsRepo.findByShortCode(shortCode);
    }
}
