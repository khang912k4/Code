package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertPostNative(Post post) {
        entityManager.createNativeQuery(
                        "INSERT INTO post (title,status,created_by,mota,pined,id_site,image) VALUES (?,?,?,?,?,?,?)")
                .setParameter(1, post.getTitle())
                .setParameter(2, "Published")
                .setParameter(3, 6)
                .setParameter(4, "uiauia")
                .setParameter(5, false)
                .setParameter(6,1)
                .setParameter(7,"https://khoacntt.ntu.edu.vn/uploads/54/images/news/6789/img/sinh-vien-cac-cac-lop-chat-luong-cao-va-he-thong-thong-tin-quan-ly-kien-tap-tai-t.jpg")
                .executeUpdate();
    }
}