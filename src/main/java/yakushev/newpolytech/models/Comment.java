package yakushev.newpolytech.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "article_id")
    private int articleId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "author_id")
    private Integer authorId;



    private boolean approved;

    public Comment() {}

    public Comment(int userId, int articleId, String text, LocalDateTime publishedAt, Integer authorId, String status) {
        this.userId = userId;
        this.articleId = articleId;
        this.text = text;
        this.publishedAt = publishedAt;
        this.authorId = authorId;
        this.approved = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getArticleId() { return articleId; }
    public void setArticleId(int articleId) { this.articleId = articleId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
