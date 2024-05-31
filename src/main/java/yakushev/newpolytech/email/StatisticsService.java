package yakushev.newpolytech.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yakushev.newpolytech.controllers.CommentController;
import yakushev.newpolytech.email.EmailService;
import yakushev.newpolytech.models.ArticleView;
import yakushev.newpolytech.models.Comment;
import yakushev.newpolytech.repository.ArticleViewRepository;
import yakushev.newpolytech.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class StatisticsService {

    @Autowired
    private ArticleViewRepository articleViewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private CommentController commentController;

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void sendArticleViewsToModerators() {
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(5);
        int viewsLast5Minutes = articleViewRepository.countArticleViewsLast5Minutes(timestamp);
        List<String> moderatorEmails = commentRepository.findEmailsByRoleId(2);

        String subject;
        String text;
        if (viewsLast5Minutes == 0) {
            subject = "Статьи не были просмотрены";
            text = "За последние 5 минут не было просмотров статей";
        } else {
            subject = "Статистика просмотров статей за последние 5 минут";
            text = "За последние 5 минут были просмотрены следующие статьи:\n\n";

            text += "Статистика просмотров статей:\n";
            List<ArticleView> articleViews = articleViewRepository.findByViewedAtAfter(timestamp);
            Map<Integer, Integer> articleViewsMap = new HashMap<>();
            for (ArticleView articleView : articleViews) {
                int articleId = articleView.getArticleId();
                int views = articleView.getViewsLast5Minutes();
                articleViewsMap.put(articleId, articleViewsMap.getOrDefault(articleId, 0) + views);
            }
            for (Map.Entry<Integer, Integer> entry : articleViewsMap.entrySet()) {
                int articleId = entry.getKey();
                int totalViews = entry.getValue();
                text += articleId + " - " + totalViews + " просмотров\n";
            }
            text += "\n";
        }

        for (String email : moderatorEmails) {
            emailService.sendEmail(email, subject, text);
        }
        articleViewRepository.resetViewsLast5Minutes();
        articleViewRepository.deleteAll();
    }

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void sendCommentsToModerators() {
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(5);
        List<Comment> commentsLast5Minutes = commentRepository.findCommentsPublishedAfter(timestamp);
        System.out.println("Comments in the last 5 minutes: " + commentsLast5Minutes);
        List<String> moderatorEmails = commentRepository.findEmailsByRoleId(2);

        String subject;
        String text;
        if (commentsLast5Minutes.isEmpty()) {
            subject = "Комментарии не были добавлены";
            text = "За последние 5 минут не было добавлено комментариев";
        } else {
            subject = "Новые комментарии за последние 5 минут";
            text = "За последние 5 минут были добавлены следующие комментарии:\n\n";

            text += "Новые комментарии:\n";
            for (Comment comment : commentsLast5Minutes) {
                text += "Статус комментария: " + comment.isApproved() + "\n";
                text += "Автор: " + comment.getAuthorId() + "\n";
                text += "Статья: " + comment.getArticleId() + "\n";
                text += "Текст: " + comment.getText() + "\n\n";
            }
        }

        for (String email : moderatorEmails) {
            emailService.sendEmail(email, subject, text);
        }
    }
}
