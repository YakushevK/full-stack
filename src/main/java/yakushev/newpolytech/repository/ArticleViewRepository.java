package yakushev.newpolytech.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yakushev.newpolytech.models.ArticleView;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, Integer> {

    @Query("SELECT COALESCE(SUM(av.viewsLast5Minutes), 0) FROM ArticleView av WHERE av.viewedAt > :timestamp")
    int countArticleViewsLast5Minutes(@Param("timestamp") LocalDateTime timestamp);

    List<ArticleView> findByViewedAtAfter(LocalDateTime timestamp);

    @Modifying
    @Transactional
    @Query("UPDATE ArticleView av SET av.viewsLast5Minutes = 0")
    void resetViewsLast5Minutes();
}
