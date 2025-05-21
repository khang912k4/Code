package com.nhom10.doanmonhoc.controller;

import com.nhom10.doanmonhoc.enums.PostStatus;
import com.nhom10.doanmonhoc.model.*;
import com.nhom10.doanmonhoc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class HomeController_FrontEnd {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private UserrRepository userrRepository;

    @Autowired
    private PageRepository pageRepository;

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Banner> banners = bannerRepository.findAllByOrderByIdAsc();
        List<Map<String, String>> bannerList = new ArrayList<>();
        for (Banner b : banners) {
            Map<String, String> bannerMap = new HashMap<>();
            bannerMap.put("image", b.getImage());
            bannerMap.put("tooltip", b.getMota());
            bannerMap.put("link", b.getIdPage() != null ? "/page/" + b.getIdPage() : "#");
            bannerList.add(bannerMap);
        }
        model.addAttribute("bannerList", bannerList);
        List<Menu> menus = menuRepository.findByIdSiteOrderByIdAsc(1L);
        model.addAttribute("menus", menus);
        Site site = siteRepository.findFirstByOrderByIdAsc();
        model.addAttribute("site", site);
        List<Post> allPosts = postRepository.findPublishedPostsOrderByPined(PostStatus.Published);
        List<Map<String, String>> posts = new ArrayList<>();
        if (!allPosts.isEmpty()) {
            Post p = allPosts.get(0);
            Map<String, String> postMap = new HashMap<>();
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("title", p.getTitle());
            postMap.put("image", p.getImage());
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        List<Page> allPages = pageRepository.findAll();
        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page page : allPages) {
            if ("Published".equalsIgnoreCase(page.getStatus())) {
                menuToPageMap.put(page.getIdMenu(), page.getId());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);
        System.out.println("MENU TO PAGE MAP: " + menuToPageMap);

        return "Frontend/index";
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa đăng";
        if (duration.toMinutes() < 60) return "Đã đăng " + duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return "Đã đăng " + duration.toHours() + " giờ trước";
        return "Đã đăng " + duration.toDays() + " ngày trước";
    }

    @GetMapping("/tin-tuc")
    public String showAllNews(Model model) {
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
        return "Frontend/news";
    }
    @GetMapping("/post/{id}")
    public String showPostDetail(@PathVariable("id") Long id, Model model) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return "redirect:/";

        Post post = postOpt.get();
        if (post.getStatus() != PostStatus.Published) return "redirect:/";

        Optional<Block> blockOpt = blockRepository.findByIdPost(post.getIdPost());
        String authorName = userrRepository.findById(post.getCreatedBy())
                .map(Userr::getFullname)
                .orElse("Không rõ");

        model.addAttribute("post", post);
        model.addAttribute("timeAgo", getTimeAgo(post.getCreatedAt()));
        model.addAttribute("contentHtml", blockOpt.map(Block::getCode).orElse(""));
        model.addAttribute("authorName", authorName);

        return "Frontend/post-detail";
    }
    @GetMapping("/page/{id}")
    public String showPage(@PathVariable("id") Long id, Model model) {
        Optional<Page> pageOpt = pageRepository.findById(id);
        if (pageOpt.isEmpty()) return "redirect:/";

        Page page = pageOpt.get();
        if (!"Published".equalsIgnoreCase(page.getStatus())) return "redirect:/";

        Optional<Block> blockOpt = blockRepository.findByIdPage(id);

        model.addAttribute("page", page);
        model.addAttribute("contentHtml", blockOpt.map(Block::getCode).orElse(""));

        return "Frontend/page-detail";
    }




}