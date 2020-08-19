package pl.cekus.antologicproject.mapper;

import org.mapstruct.Mapper;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.model.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project mapProjectCreateFormToProject(ProjectCreateForm createForm);

    ProjectDto mapProjectToProjectDto(Project project);
}
