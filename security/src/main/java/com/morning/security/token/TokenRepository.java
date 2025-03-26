package com.morning.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and t.chatId = null and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidNonTelegramTokenByUser(Long id);

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and t.chatId = :chatId and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTelegramTokenByUser(Long id, Long chatId);

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and t.chatId = :chatId and (t.expired = false or t.revoked = false)\s
            """)
    Optional<Token> findByChatId(Long chatId);

    Optional<Token> findByToken(String token);
}
