package com.devee.devhive.domain.project.comment.repository;

import com.devee.devhive.domain.project.comment.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findAllByProjectId(Long projectId);
}
