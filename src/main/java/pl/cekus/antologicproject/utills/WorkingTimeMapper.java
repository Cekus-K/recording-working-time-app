package pl.cekus.antologicproject.utills;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.model.WorkingTime;

@Mapper(componentModel = "spring")
public interface WorkingTimeMapper {

    @Mapping(source = "project.projectName", target = "project")
    WorkingTimeDto mapWorkingTimeToWorkingTimeDto(WorkingTime workingTime);
}
