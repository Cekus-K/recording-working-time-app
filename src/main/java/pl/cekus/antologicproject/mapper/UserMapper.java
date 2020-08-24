package pl.cekus.antologicproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapUserToUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "workingTimes", ignore = true)
    User mapUserCreateFormToUser(UserCreateForm createForm);

    void fromUserCreateFormToUser(UserCreateForm userCreateForm, @MappingTarget User user);
}
