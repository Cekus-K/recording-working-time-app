package pl.cekus.antologicproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.cekus.antologicproject.model.Role;

@Getter
@AllArgsConstructor
public class UserDto {

    private String login;

    private String firstName;

    private String lastName;

    private Role role;

    private String email;

    private Double costPerHour;
}
