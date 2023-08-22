package com.devee.devhive.domain.project.comment.reply.entity.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyForm {

    @NotBlank
    @Size(min = 1, max = 101)
    private String content;

}
