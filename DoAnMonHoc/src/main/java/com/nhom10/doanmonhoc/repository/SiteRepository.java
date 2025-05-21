package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
    Site findFirstByOrderByIdAsc();
}