package yakushev.newpolytech.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_views")
public class ArticleView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "article_id")
    private int articleId;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "views_last_5_minutes")
    private int viewsLast5Minutes;

    public ArticleView() {}

    public ArticleView(int articleId, LocalDateTime viewedAt, int viewsLast5Minutes) {
        this.articleId = articleId;
        this.viewedAt = viewedAt;
        this.viewsLast5Minutes = viewsLast5Minutes;
    }

    public ArticleView(int articleId, int viewsLast5Minutes) {
        this.articleId = articleId;
        this.viewsLast5Minutes = viewsLast5Minutes;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getArticleId() {
        return articleId;
    }
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    public int getViewsLast5Minutes() {
        return viewsLast5Minutes;
    }
    public void setViewsLast5Minutes(int viewsLast5Minutes) {
        this.viewsLast5Minutes = viewsLast5Minutes;
    }
}
