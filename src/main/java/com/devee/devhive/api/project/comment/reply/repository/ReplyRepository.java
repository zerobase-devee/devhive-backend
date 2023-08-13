package com.devee.devhive.api.project.comment.reply.repository;

import com.devee.devhive.api.project.comment.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

}
