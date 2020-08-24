package pl.cekus.antologicproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.model.WorkingTime;

@Mapper(componentModel = "spring")
public interface WorkingTimeMapper {

    @Mapping(source = "project.projectName", target = "project")
    WorkingTimeDto mapWorkingTimeToWorkingTimeDto(WorkingTime workingTime);

    void fromWorkingTimeCreateFormToWorkingTime(WorkingTimeCreateForm workingTimeCreateForm,
                                                @MappingTarget WorkingTime workingTime);
}
