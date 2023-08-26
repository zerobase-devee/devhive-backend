package com.devee.devhive.domain.project.comment.reply.repository;

import com.devee.devhive.domain.project.comment.reply.entity.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
  List<Reply> findAllByCommentId(Long commentId);
}
