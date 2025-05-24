package com.nhom10.doanmonhoc.controller;

import com.nhom10.doanmonhoc.enums.PostStatus;
import com.nhom10.doanmonhoc.model.*;
import com.nhom10.doanmonhoc.repository.PostRepository;
import com.nhom10.doanmonhoc.repository.SiteRepository;
import com.nhom10.doanmonhoc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class HomeController_Backend {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private BlockService blockService;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private SiteService siteService;

    @GetMapping("/allsite")
    public String sites(Model model) {
        List<Map<String,String>> sites = new ArrayList<>();
        for (Site s: siteRepository.findAllByOrderByIdSiteAsc()){
            Map<String, String> siteMap = new HashMap<>();
            siteMap.put("site_name",s.getName());
            siteMap.put("last_published",getTimeAgo(s.getUpdatedAt()));
            siteMap.put("link","/site/"+s.getIdSite());
            siteMap.put("link_edit","/back/"+s.getIdSite());
            sites.add(siteMap);
        }
        model.addAttribute("sites", sites);
        return "Backend/allSites";
    }
    @PostMapping("/allsite")
    public String allSites(Model model,@RequestParam(value = "btn-add",required = false) String cl) {
        if(!Objects.equals(cl, null)){
            Site site = new Site();
            site.setName("My Site Style");
            siteService.insertSiteNative(site);
        }
        List<Map<String,String>> sites = new ArrayList<>();
        for (Site s: siteRepository.findAllByOrderByIdSiteAsc()){
            Map<String, String> siteMap = new HashMap<>();
            siteMap.put("site_name",s.getName());
            siteMap.put("last_published",getTimeAgo(s.getUpdatedAt()));
            siteMap.put("link","/site/"+s.getIdSite());
            siteMap.put("link_edit","/back/"+s.getIdSite());
            sites.add(siteMap);
        }
        model.addAttribute("sites", sites);
        return "Backend/allSites";
    }

    @GetMapping("/back/{id}")
    public String allPosts(@PathVariable("id") Long id,Model model) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        return "Backend/index";
    }
    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa đăng";
        if (duration.toMinutes() < 60) return "Đã đăng " + duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return "Đã đăng " + duration.toHours() + " giờ trước";
        return "Đã đăng " + duration.toDays() + " ngày trước";
    }
    @GetMapping("/allpost/{id}")
    public String allPost(@PathVariable("id") Long id,Model model) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        List<Map<String, String>> posts = new ArrayList<>();
        for (Post p : postRepository.findPublishedPostsByIdSiteOrderByPined(id,PostStatus.Published)) {
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
    @GetMapping("/addpost/{id}")
    public String addPost(@PathVariable("id") Long id,Model model) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        return "Backend/addPost";
    }

    @PostMapping("/addpost/{id}")
    public String addPostLinhTinh(@PathVariable("id") Long id,
                                  Model model,
                                  @RequestParam("block") List<String> block_content,
                                  @RequestParam("title") String title,
                                  @RequestParam(value = "btn-publish",required = false) String cl
    ) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        if(!Objects.equals(cl, null)){
            Post post = new Post();
            post.setTitle(title);
            post.setIdSite(site.getIdSite());
            post.setStatus(PostStatus.Published);
            post.setMota("Web gi day?");
            post.setPined(false);
            post.setCreatedBy(6L);
            postService.insertPostNative(post);
            Post insertedPost = postRepository.findTopByOrderByIdPostDesc();
            for (String blockContent : block_content) {
                if(blockContent!=null && !blockContent.isEmpty()) {
                    Block block = new Block();
                    block.setCode(blockContent);
                    block.setIdPost(insertedPost.getIdPost());
                    block.setIdPage(null);
                    blockService.insertBlockNative(block);
                }
            }
        }
        return "Backend/addPost";
    }


}