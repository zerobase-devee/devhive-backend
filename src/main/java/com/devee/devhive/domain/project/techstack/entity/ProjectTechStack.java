package com.devee.devhive.domain.project.techstack.entity;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.techstack.entity.TechStack;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProjectTechStack {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tech_stack_id")
  private TechStack techStack;

  public static ProjectTechStack of(Project project, TechStack techStack) {
    return ProjectTechStack.builder()
        .project(project)
        .techStack(techStack)
        .build();
  }
}
