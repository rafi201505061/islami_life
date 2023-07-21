package life.islami.app.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import life.islami.app.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, length = 12, unique = true)
  private String userId;

  @Column(nullable = false, length = 250, unique = true)
  private String userName;

  @Column(nullable = false, length = 200)
  private String fullName;

  @Column(length = 20)
  private String phoneNo;

  @Column(length = 250)
  private String email;

  @Column(length = 70, name = "password")
  private String encryptedPassword;

  @Column(length = 50)
  private String profilePictureUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(length = 10)
  private String passwordResetToken;
}
