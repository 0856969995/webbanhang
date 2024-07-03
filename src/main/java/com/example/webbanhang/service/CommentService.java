package com.example.webbanhang.service;

import com.example.webbanhang.model.Comment;
import com.example.webbanhang.model.Product;
import com.example.webbanhang.repository.CommentRepository;
import com.example.webbanhang.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment replyToComment(Long parentId, Comment reply) {
        Comment parentComment = commentRepository.findById(parentId).orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        reply.setParent(parentComment);
        return commentRepository.save(reply);
    }

    public Comment updateComment(Long id, Comment commentDetails) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setContent(commentDetails.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }
    @Autowired
    private ProductRepository productRepository;
    public Comment addCommentToProduct(Long productId, Comment comment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        // Thiết lập mối quan hệ giữa bình luận và sản phẩm
        comment.setProduct(product);
        // Lưu bình luận vào cơ sở dữ liệu
        return commentRepository.save(comment);
    }

}
