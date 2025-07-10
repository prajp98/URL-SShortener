package com.example.urlshortener.repository;

import com.example.urlshortener.model.UrlAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrlAnalyticsRepository extends JpaRepository<UrlAnalytics, Long> {
    List<UrlAnalytics> findByShortCode(String shortCode);
}

