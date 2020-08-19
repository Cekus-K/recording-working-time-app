package pl.cekus.antologicproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.cekus.antologicproject.model.Role;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID uuid;

    private String login;

    private String firstName;

    private String lastName;

    private Role role;

    private String email;

    private BigDecimal costPerHour;
}
