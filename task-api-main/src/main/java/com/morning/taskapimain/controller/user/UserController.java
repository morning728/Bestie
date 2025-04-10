package com.morning.taskapimain.controller.user;

import com.morning.taskapimain.entity.dto.AboutMeDTO;
import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.entity.user.Contacts;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.UserService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * ✅ Получение информации о пользователе по username
     */
    @GetMapping("/{username}")
    public Mono<ResponseEntity<User>> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Получение информации о пользователе по username
     */
    @GetMapping("/find")
    public Flux<UserDTO> getUsersByStartWith(@RequestParam(name = "start-with") String startWith) {
        return userService.getUsersByStartWith(startWith);
    }

    /**
     * ✅ Получение информации о пользователе по ID
     */
    @GetMapping("/id/{userId}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Проверка существования пользователя по username
     */
    @GetMapping("/{username}/exists")
    public Mono<ResponseEntity<Boolean>> userExists(@PathVariable String username) {
        return userService.userExists(username)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Получение всех пользователей
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * ✅ Получение контактов пользователя по username
     */
/*    @GetMapping("/{username}/contacts")
    public Mono<ResponseEntity<Contacts>> getUserContactsByUsername(@PathVariable String username) {
        return userService.getUserContactsByUsername(username)
                .map(ResponseEntity::ok);
    }*/

    /**
     * ✅ Обновление информации о пользователе
     */
    @PutMapping("/{username}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String username,
                                                 @RequestBody User updatedUser,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return userService.updateUser(username, updatedUser, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Удаление пользователя
     */
    @DeleteMapping("/{username}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String username,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return userService.deleteUser(username, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * ✅ Получение контактов пользователя через WebClient
     */
    @GetMapping("/{username}/contacts")
    public Mono<ResponseEntity<Contacts>> findProfileByUsernameWithWebClient(@PathVariable String username,
                                                                               @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return userService.findProfileByUsernameWithWebClient(username, token)
                .map(ResponseEntity::ok);
    }

    /**
     *
     */
    @GetMapping("/me")
    public Mono<AboutMeDTO> findMe(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return userService.getUserByToken(token)
                .map(AboutMeDTO::fromUser);
    }
}
