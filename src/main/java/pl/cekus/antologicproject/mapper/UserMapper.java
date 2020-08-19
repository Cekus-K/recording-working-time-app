package pl.cekus.antologicproject.mapper;

import org.mapstruct.Mapper;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapUserToUserDto(User user);

    User mapUserCreateFormToUser(UserCreateForm createForm);
}
