package explore.with.me.user;

import explore.with.me.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        UserDto saveUserDto = userService.createUser(userDto);
        return saveUserDto;
    }

    @GetMapping
    public List<UserDto> readUsers(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<UserDto> usersDto = userService.getUsers(ids, from, size);
        return usersDto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
