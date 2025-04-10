package com.morning.security.token;

import com.morning.security.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;

  public boolean expired;

  @Column(name = "chat_id", nullable = true)
  private Long chatId; // Связь с Telegram

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auth_user_id")
  public User user;
}
