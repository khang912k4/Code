package com.nhom10.doanmonhoc.controller;

import com.nhom10.doanmonhoc.enums.PostStatus;
import com.nhom10.doanmonhoc.model.Block;
import com.nhom10.doanmonhoc.model.Post;
import com.nhom10.doanmonhoc.repository.PostRepository;
import com.nhom10.doanmonhoc.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController_Backend {
    @Autowired
    private PostService PostService;
    @Autowired
    private PostRepository postRepository;
    @GetMapping("/back")
    public String allPosts() {
        return "Backend/index";
    }
    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa đăng";
        if (duration.toMinutes() < 60) return "Đã đăng " + duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return "Đã đăng " + duration.toHours() + " giờ trước";
        return "Đã đăng " + duration.toDays() + " ngày trước";
    }
    @GetMapping("/allpost")
    public String allPost(Model model) {
        List<Map<String, String>> posts = new ArrayList<>();
        for (Post p : postRepository.findPublishedPostsOrderByPined(PostStatus.Published)) {
            Map<String, String> postMap = new HashMap<>();
            postMap.put("title", p.getTitle());
            postMap.put("image", p.getImage());
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);

        }
        model.addAttribute("posts", posts);
        return "Backend/allPosts";
    }
    @GetMapping("/addpost")
    public String addPost() {
        return "Backend/addPost";
    }
    @PostMapping("/addpost")
    public String addPostLinhTinh(Model model, Post post, @RequestParam("block") String block_content, @RequestParam("title") String title) {
        post.setTitle(title);
//        block.setCode(block_content);
        PostService.insertPostNative(post);
        System.out.println(block_content);
        return "Backend/addPost";
    }

}
