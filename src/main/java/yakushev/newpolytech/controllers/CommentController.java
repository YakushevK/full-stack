package yakushev.newpolytech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yakushev.newpolytech.email.EmailService;
import yakushev.newpolytech.models.Comment;
import yakushev.newpolytech.payload.response.MessageResponse;
import yakushev.newpolytech.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmailService emailService;


    @PostMapping("/save")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> saveComment(@RequestBody Comment comment) {
        comment.setPublishedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        System.out.println("Saved comment: " + savedComment);

        List<String> moderatorEmails = commentRepository.findEmailsByRoleId(2);
        String subject = "New Comment Notification";
        String text = "A new comment has been added. Please review it.";

        for (String email : moderatorEmails) {
            emailService.sendEmail(email, subject, text);
        }
        return ResponseEntity.ok(new MessageResponse("Comment saved successfully!"));
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or @commentRepository.findById(#id).orElse(null)?.getAuthorId() == authentication.principal.id or @commentRepository.findById(#id).orElse(null)?.getArticle().getAuthorId() == authentication.principal.id")
    public ResponseEntity<?> deleteComment(@PathVariable int id) {
        commentRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Comment deleted successfully!"));
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> approveComment(@PathVariable int id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        comment.setApproved(true);
        commentRepository.save(comment);

        return ResponseEntity.ok(new MessageResponse("Comment approved successfully!"));
    }

    @GetMapping("/unapproved")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public List<Comment> getUnapprovedComments() {
        return commentRepository.findByApprovedFalse();
    }

    @GetMapping("/article/{articleId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Comment> getApprovedCommentsByArticleId(@PathVariable int articleId) {
        return commentRepository.findByArticleIdAndApprovedTrue(articleId);
    }
}
