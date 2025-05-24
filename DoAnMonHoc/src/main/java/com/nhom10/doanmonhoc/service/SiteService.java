package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Site;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertSiteNative(Site site) {
        entityManager.createNativeQuery(
                        "INSERT INTO site (site_name,logo) VALUES (?,?)")
                .setParameter(1, site.getName())
                .setParameter(2, "https://i.pinimg.com/236x/85/40/33/854033242929cb15cd206e07b3981d58.jpg")
                .executeUpdate();
    }
}