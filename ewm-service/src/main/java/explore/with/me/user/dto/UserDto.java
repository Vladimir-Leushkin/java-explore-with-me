package explore.with.me.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @NonNull
    private String email;
}
