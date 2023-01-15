package explore.with.me.user;

import explore.with.me.user.dto.UserDto;
import explore.with.me.user.dto.UserDtoShort;
import explore.with.me.user.model.User;


public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDtoShort toUserDtoShort(User user) {
        return new UserDtoShort(
                user.getId(),
                user.getName()
        );
    }

}