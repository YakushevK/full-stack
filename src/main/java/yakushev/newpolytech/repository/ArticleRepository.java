package yakushev.newpolytech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yakushev.newpolytech.models.Article;

import java.util.Date;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findByPublished(boolean published);
    List<Article> findByTitleContaining(String title);
    @Query("SELECT a FROM Article a WHERE a.publishedAt > :timestamp")
    List<Article> findArticlesPublishedAfter(@Param("timestamp") Date timestamp);
}