package com.woowacourse.sunbook.application.service;

import com.woowacourse.sunbook.MockStorage;
import com.woowacourse.sunbook.application.dto.comment.CommentResponseDto;
import com.woowacourse.sunbook.application.exception.NotFoundArticleException;
import com.woowacourse.sunbook.domain.Content;
import com.woowacourse.sunbook.domain.article.Article;
import com.woowacourse.sunbook.domain.comment.Comment;
import com.woowacourse.sunbook.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CommentServiceTest extends MockStorage {
    private static final Long ID = 1L;

    @InjectMocks
    private CommentService injectCommentService;

    @Test
    void 특정_게시글_댓글수_조회() {
        given(commentRepository.countByArticleId(1L)).willReturn(2L);

        injectCommentService.countByArticleId(1L);

        verify(commentRepository).countByArticleId(1L);
    }

    @Test
    void 댓글_작성() {
        given(userService.findById(ID)).willReturn(mock(User.class));
        given(articleService.findById(ID)).willReturn(mock(Article.class));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        given(modelMapper.map(mock(Comment.class), CommentResponseDto.class)).willReturn(mock(CommentResponseDto.class));

        injectCommentService.save(mock(Content.class), ID, ID, null);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void 대댓글_작성() {
        given(userService.findById(ID)).willReturn(user);
        given(articleService.findById(ID)).willReturn(article);
        given(commentRepository.findById(ID)).willReturn(Optional.of(comment));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        given(modelMapper.map(comment, CommentResponseDto.class)).willReturn(commentResponseDto);

        injectCommentService.save(content, ID, ID, ID);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void 대댓글_조회() {
        given(commentRepository.findById(ID)).willReturn(Optional.of(comment));
        given(commentService.findById(ID)).willReturn(comment);

        injectCommentService.findByIdAndArticleId(ID, ID);

        verify(commentRepository).findByParentAndArticleId(any(Comment.class), any(Long.class));
    }

    @Test
    void 없는_게시글_댓글_수정() {
        given(userService.findById(ID)).willReturn(mock(User.class));
        given(articleService.findById(ID)).willThrow(NotFoundArticleException.class);

        assertThrows(NotFoundArticleException.class, () -> {
            injectCommentService.modify(ID, mock(Content.class), ID, ID);
        });
    }

    @Test
    void 게시글_댓글_수정_성공() {
        given(userService.findById(ID)).willReturn(mock(User.class));
        given(articleService.findById(ID)).willReturn(mock(Article.class));
        given(commentRepository.findById(ID)).willReturn(Optional.of(comment));
        doNothing().when(mock(Comment.class)).validateAuth(mock(User.class), mock(Article.class));
        given(modelMapper.map(mock(Comment.class), CommentResponseDto.class)).willReturn(mock(CommentResponseDto.class));

        injectCommentService.modify(ID, mock(Content.class), ID, ID);

        verify(comment).modify(any(Content.class), any(User.class), any(Article.class));
    }

    @Test
    void 없는_게시글_댓글_삭제() {
        given(userService.findById(ID)).willReturn(mock(User.class));
        given(articleService.findById(ID)).willThrow(NotFoundArticleException.class);

        assertThrows(NotFoundArticleException.class, () -> {
            injectCommentService.remove(ID, ID, ID);
        });
    }

    @Test
    void 게시글_댓글_삭제_성공() {
        given(userService.findById(ID)).willReturn(mock(User.class));
        given(articleService.findById(ID)).willReturn(mock(Article.class));
        given(commentRepository.findById(ID)).willReturn(Optional.of(mock(Comment.class)));
        doNothing().when(mock(Comment.class)).validateAuth(mock(User.class), mock(Article.class));
        doNothing().when(commentRepository).delete(mock(Comment.class));
        given(modelMapper.map(mock(Comment.class), CommentResponseDto.class)).willReturn(mock(CommentResponseDto.class));

        injectCommentService.remove(ID, ID, ID);

        verify(commentRepository).delete(any(Comment.class));
    }
}