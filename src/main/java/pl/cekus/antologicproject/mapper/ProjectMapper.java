package pl.cekus.antologicproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.ProjectViewDto;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.ProjectView;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "workingTimes", ignore = true)
    Project mapProjectCreateFormToProject(ProjectCreateForm createForm);

    ProjectDto mapProjectToProjectDto(Project project);

    ProjectViewDto mapProjectViewToProjectViewDto(ProjectView projectView);

    void fromProjectCreateFormToProject(ProjectCreateForm projectCreateForm, @MappingTarget Project project);
}
