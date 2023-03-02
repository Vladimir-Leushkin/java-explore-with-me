package explore.with.me.user;

import explore.with.me.exception.ConflictException;
import explore.with.me.exception.ValidationException;
import explore.with.me.user.dto.UserDto;
import explore.with.me.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class  UserService {
    private final UserRepository repository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user;
        try {
            user = UserMapper.toUser(userDto);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Указан неверный формат электронной почты");
        }
        if (user.getName() == null) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
        if (user.getEmail() == null) {
            throw new ValidationException("Адрес электронной почты не может быть пустым");
        }
        try {
            User saveUser = repository.save(user);
            log.info("Добавлен новый пользователь : {}", user);
            return UserMapper.toUserDto(saveUser);
        } catch (DataIntegrityViolationException e) {
            log.info("Пользователь с такой электронной почтой уже существует {}", user.getEmail());
            throw new ConflictException("Пользователь с такой электронной почтой уже существует");
        }

    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = pagination(from, size);
        List<User> users = new ArrayList<>();
        if (ids.size() != 0) {
            for (Long id : ids) {
                User user = getUser(id);
                users.add(user);
            }
        } else {
            users.addAll(repository.findAll(pageRequest).toList());
        }
        log.info("Найдены пользователи {}, ", users);
        List<UserDto> usersDto = new ArrayList<>();
        if (users.get(0) != null) {
            usersDto = users.stream()
                    .map(user -> UserMapper.toUserDto(user))
                    .collect(Collectors.toList());
        }
        return usersDto;
    }

    public User getUser(Long userId) {
        User user = repository.findById(userId)
                .orElse(null);
        log.info("Найден пользователь : {}", user);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        return users;
    }

    @Transactional
    public User updateUser(Long userId, UserDto newUserDto) {
        User user = UserMapper.toUser(newUserDto);
        User oldUser = getUser(userId);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        try {
            User saveUser = repository.save(oldUser);
            log.info("Обновлен пользователь : {}", saveUser);
            return saveUser;
        } catch (DataIntegrityViolationException e) {
            log.info("Пользователь с таким email уже существует {}", user.getEmail());
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        log.info("Удален пользователь : {}", user);
        repository.deleteById(userId);
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }

}
