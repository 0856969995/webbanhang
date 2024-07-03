package com.example.webbanhang.repository;

import com.example.webbanhang.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CommentRepository extends JpaRepository<Comment,Long> {
}
