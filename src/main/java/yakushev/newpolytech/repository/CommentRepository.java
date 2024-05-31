package yakushev.newpolytech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yakushev.newpolytech.models.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByArticleId(int articleId);
    List<Comment> findByApprovedFalse();
    List<Comment> findByArticleIdAndApprovedTrue(int articleId);
    void deleteByArticleId(int articleId);

    @Query("SELECT u.email FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<String> findEmailsByRoleId(@Param("roleId") int roleId);

    @Query("SELECT c FROM Comment c WHERE c.publishedAt > :timestamp")
    List<Comment> findCommentsPublishedAfter(@Param("timestamp") LocalDateTime timestamp);
}

