package com.nhom10.doanmonhoc.controller;

import com.nhom10.doanmonhoc.enums.PostStatus;
import com.nhom10.doanmonhoc.model.*;
import com.nhom10.doanmonhoc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //    @GetMapping("/")
//    public String showHomePage(Model model) {
//        List<Banner> banners = bannerRepository.findAllByOrderByIdBannerAsc();
//        List<Map<String, String>> bannerList = new ArrayList<>();
//        for (Banner b : banners) {
//            Map<String, String> bannerMap = new HashMap<>();
//            bannerMap.put("image", b.getImage());
//            bannerMap.put("tooltip", b.getMota());
//            bannerMap.put("link", b.getIdSite() != null ? "/page/" + b.getIdSite() : "#");
//            bannerList.add(bannerMap);
//        }
//        model.addAttribute("bannerList", bannerList);
//        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(1L);
//        model.addAttribute("menus", menus);
//        Site site = siteRepository.findFirstByOrderByIdSiteAsc();
//        model.addAttribute("site", site);
//        List<Post> allPosts = postRepository.findPublishedPostsOrderByPined(PostStatus.Published);
//        List<Map<String, String>> posts = new ArrayList<>();
//        if (!allPosts.isEmpty()) {
//            Post p = allPosts.get(0);
//            Map<String, String> postMap = new HashMap<>();
//            postMap.put("id", String.valueOf(p.getIdPost()));
//            postMap.put("title", p.getTitle());
//            postMap.put("image", p.getImage());
//            postMap.put("tooltip", p.getMota());
//            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
//            postMap.put("link", "/post/" + p.getIdPost());
//            posts.add(postMap);
//        }
//        model.addAttribute("posts", posts);
//
//        List<Page> allPages = pageRepository.findAll();
//        Map<Long, Long> menuToPageMap = new HashMap<>();
//        for (Page page : allPages) {
//            if ("Published".equalsIgnoreCase(page.getStatus())) {
//                menuToPageMap.put(page.getIdMenu(), page.getIdPage());
//            }
//        }
//        model.addAttribute("menuToPageMap", menuToPageMap);
//        System.out.println("MENU TO PAGE MAP: " + menuToPageMap);
//
//        return "Frontend/index";
//    }
    private boolean isValidImageUrl(String url) {
        return url != null && url.matches("^https?://.*\\.(png|jpg|jpeg|gif|webp)$");
    }
    private String extractImageFromBlocks(List<Block> blocks) {
        String fallback = "https://xdcs.cdnchinhphu.vn/446259493575335936/2024/8/17/nhatrang1-17238902889991160055539.jpg";
        for (Block block : blocks) {
            if (block.getCode() != null && block.getCode().contains("<img")) {
                Matcher matcher = Pattern.compile("src\\s*=\\s*\"([^\"]+)\"").matcher(block.getCode());
                if (matcher.find()) {
                    String imgUrl = matcher.group(1);
                    if (isValidImageUrl(imgUrl)) {
                        return imgUrl;
                    }
                }
            }
        }
        return fallback;
    }



    @GetMapping("/site/{id}")
    public String showHomePage(Model model,@PathVariable("id") Long id) throws IOException {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        byte [] data = (site.getLogo()).getBytes();
        System.out.printf("So luong %d/n",data.length);
//        ByteArrayInputStream bais = new ByteArrayInputStream(data);
//        BufferedImage bImage = ImageIO.read(bais);
//        ImageIO.write(bImage, "jpg", new File("/Users/khang912k4/Documents/DoAnMonHoc/src/main/image/output.JPG"));
        List<Banner> banners = bannerRepository.findByIdSiteOrderByIdBannerAsc(site.getIdSite());
        List<Map<String, String>> bannerList = new ArrayList<>();
        for (Banner b : banners) {
            Map<String, String> bannerMap = new HashMap<>();
            bannerMap.put("image", b.getImage());
            bannerMap.put("tooltip", b.getMota());
            bannerList.add(bannerMap);
        }
        model.addAttribute("bannerList", bannerList);
        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(site.getIdSite());
        model.addAttribute("menus", menus);
        model.addAttribute("site", site);
        List<Post> allPosts = postRepository.findPublishedPostsByIdSiteOrderByPined(site.getIdSite(),PostStatus.Published);
        List<Map<String, String>> posts = new ArrayList<>();
        if (!allPosts.isEmpty()) {
            Post p = allPosts.get(0);
            Map<String, String> postMap = new HashMap<>();
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("title", p.getTitle());
            List<Block> blocks = blockRepository.findByIdPost(p.getIdPost());
            String imageUrl = extractImageFromBlocks(blocks);
            postMap.put("image", imageUrl);
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
                menuToPageMap.put(page.getIdMenu(), page.getIdPage());
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

    @GetMapping("/tin-tuc/{id}")
    public String showAllNews(Model model, @PathVariable("id") Long id) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";
        Site site = siteOpt.get();
        model.addAttribute("site", site);
        model.addAttribute("pageCss", "/css/Frontend/news.css");

        List<Map<String, String>> posts = new ArrayList<>();
        for (Post p : postRepository.findPublishedPostsByIdSiteOrderByPined(id, PostStatus.Published)) {
            Map<String, String> postMap = new HashMap<>();
            postMap.put("title", p.getTitle());
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("pined", String.valueOf(p.getPined()));
            List<Block> blocks = blockRepository.findByIdPost(p.getIdPost());
            String imageUrl = extractImageFromBlocks(blocks);
            postMap.put("image", imageUrl);
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        // Thêm menu
        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(id);
        model.addAttribute("menus", menus);

        // Thêm menuToPageMap
        List<Page> allPages = pageRepository.findAll();
        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page page : allPages) {
            if ("Published".equalsIgnoreCase(page.getStatus())) {
                menuToPageMap.put(page.getIdMenu(), page.getIdPage());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);

        return "Frontend/news";
    }




    @GetMapping("/post/{id}")
    public String showPostDetail(@PathVariable("id") Long id, Model model) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return "redirect:/";

        Post post = postOpt.get();
        if (post.getStatus() != PostStatus.Published) return "redirect:/";

        List<Map<String, String>> blocks = new ArrayList<>();
        for (Block block : blockRepository.findByIdPost(post.getIdPost())) {
            Map<String, String> blockMap = new HashMap<>();
            blockMap.put("code",block.getCode());
            blocks.add(blockMap);
        }
        String authorName = userrRepository.findById(post.getCreatedBy())
                .map(Userr::getFullname)
                .orElse("Không rõ");

        model.addAttribute("post", post);
        model.addAttribute("timeAgo", getTimeAgo(post.getCreatedAt()));
        model.addAttribute("contentHtml", blocks);
        model.addAttribute("authorName", authorName);

        return "Frontend/post-detail";
    }

    private void injectCommonModelAttributes(Model model) {
        Site site = siteRepository.findFirstByOrderByIdSiteAsc();
        model.addAttribute("site", site);

        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(site.getIdSite());
        model.addAttribute("menus", menus);

        List<Page> allPages = pageRepository.findAll();
        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page p : allPages) {
            if ("Published".equalsIgnoreCase(p.getStatus())) {
                menuToPageMap.put(p.getIdMenu(), p.getIdPage());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);
    }


    @GetMapping("/page/{id}")
    public String showPage(@PathVariable("id") Long id, Model model) {
        Optional<Page> pageOpt = pageRepository.findById(id);
        if (pageOpt.isEmpty()) return "redirect:/";

        Page page = pageOpt.get();
        if (!"Published".equalsIgnoreCase(page.getStatus())) return "redirect:/";

        List<Map<String, String>> blocks = new ArrayList<>();
        for (Block block : blockRepository.findByIdPage(page.getIdPage())) {
            Map<String, String> blockMap = new HashMap<>();
            blockMap.put("code",block.getCode());
            blocks.add(blockMap);
        }

        model.addAttribute("page", page);
        model.addAttribute("contentHtml", blocks);

        injectCommonModelAttributes(model);
        return "Frontend/page-detail";
    }




}