package com.example.webbanhang.controller;

import com.example.webbanhang.model.Comment;
import com.example.webbanhang.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment newComment = commentService.createComment(comment);
        return ResponseEntity.ok(newComment);
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<Comment> replyToComment(@PathVariable Long id, @RequestBody Comment reply) {
        Comment newReply = commentService.replyToComment(id, reply);
        return ResponseEntity.ok(newReply);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment commentDetails) {
        Comment updatedComment = commentService.updateComment(id, commentDetails);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @PostMapping("/products/{productId}/comments")
    public Comment addComment(@PathVariable Long productId, @RequestBody Comment comment) {
        // Save comment to database
        // Optionally, broadcast new comment to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/comments/" + productId, comment);
        return comment;
    }
}
