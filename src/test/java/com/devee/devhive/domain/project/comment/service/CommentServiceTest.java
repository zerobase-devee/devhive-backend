package com.devee.devhive.domain.project.comment.service;

import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.comment.entity.Comment;
import com.devee.devhive.domain.project.comment.entity.form.CommentForm;
import com.devee.devhive.domain.project.comment.repository.CommentRepository;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

class CommentServiceTest {
  @InjectMocks
  private CommentService commentService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private CommentRepository commentRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("댓글 생성 - 성공")
  void testCreate() {
    // Given
    User user = User.builder().id(1L).build();
    User writer = User.builder().id(3L).build();
    Project project = Project.builder().user(writer).build();
    CommentForm form = new CommentForm("안녕하세요");
    // When
    commentService.create(user, project, form);
    // Then
    verify(eventPublisher, times(1)).publishEvent(any(AlarmForm.class));
  }

  @Test
  @DisplayName("댓글 수정 - 성공")
  void testUpdate_ValidUserAndCommentId() {
    // Given
    User user = new User();
    user.setId(1L);
    Comment comment = new Comment();
    comment.setUser(user);
    CommentForm form = new CommentForm("Updated Content");
    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    // When
    Comment result = commentService.update(user, 1L, form);

    // Then
    assertNotNull(result);
    assertEquals(form.getContent(), result.getContent());
  }

  @Test
  @DisplayName("댓글 수정 - 실패_댓글 작성자가 아님")
  void testUpdate_InvalidUser() {
    // Given
    User user = new User();
    user.setId(1L);
    User anotherUser = new User();
    anotherUser.setId(2L);
    Comment comment = new Comment();
    comment.setUser(anotherUser);
    CommentForm form = new CommentForm("Updated Content");
    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

    // When
    CustomException exception = assertThrows(CustomException.class,
        () -> commentService.update(user, 1L, form));

    // Then
    assertEquals(UNAUTHORIZED, exception.getErrorCode());
  }
}