package yakushev.newpolytech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import yakushev.newpolytech.email.EmailService;
import yakushev.newpolytech.models.Article;
import yakushev.newpolytech.models.ArticleView;
import yakushev.newpolytech.payload.response.MessageResponse;
import yakushev.newpolytech.repository.ArticleRepository;
import yakushev.newpolytech.repository.CommentRepository;
import yakushev.newpolytech.repository.ArticleViewRepository;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ArticleViewRepository articleViewRepository;



    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> saveArticle(@RequestBody Article article) {
        articleRepository.save(article);
        return ResponseEntity.ok(new MessageResponse("Article saved successfully!"));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Transactional
    public ResponseEntity<?> deleteArticle(@PathVariable int id) {
        commentRepository.deleteByArticleId(id);
        articleRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Article and related comments deleted successfully!"));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateArticle(@PathVariable int id, @RequestBody Article updatedArticle) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        article.setTitle(updatedArticle.getTitle());
        article.setDescription(updatedArticle.getDescription());
        article.setUpdatedAt(new Date());
        articleRepository.save(article);

        return ResponseEntity.ok(new MessageResponse("Article updated successfully!"));
    }

    @PutMapping("/publish/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> publishArticle(@PathVariable int id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        if (!article.isPublished()) {
            article.setPublished(true);
            articleRepository.save(article);
            incrementArticleViews(id);
            List<String> userMails = commentRepository.findEmailsByRoleId(1);
            String subject = "New Article Notification";
            String text = "A new article has been published. Check it out!";
            for (String mail : userMails) {
                emailService.sendEmail(mail, subject, text);
            }
        }

        return ResponseEntity.ok(new MessageResponse("Article published successfully!"));
    }

    @GetMapping("/published")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Article> getPublishedArticles() {
        return articleRepository.findByPublished(true);
    }

    @GetMapping("/unpublished")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public List<Article> getUnpublishedArticles() {
        return articleRepository.findByPublished(false);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable int id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
        incrementArticleViews(id);
        return ResponseEntity.ok(article);
    }


    @GetMapping("/search")
    public List<Article> searchArticleByTitle(@RequestParam String title) {
        return articleRepository.findByTitleContaining(title);
    }

    @GetMapping("/author/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<String> getAuthorNameById(@PathVariable int id) {
        String  authorName = getAuthorLogin(id);
        return ResponseEntity.ok(authorName);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getAuthorLogin(int authorId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{authorId}, String.class);
    }


    private void incrementArticleViews(int articleId) {
        ArticleView articleView = articleViewRepository.findById(articleId).orElse(null);

        if (articleView == null) {
            articleView = new ArticleView(articleId, LocalDateTime.now(), 1);
        } else {
            articleView.setViewedAt(LocalDateTime.now());
            articleView.setViewsLast5Minutes(articleView.getViewsLast5Minutes() + 1);
        }

        articleViewRepository.save(articleView);
    }



}
