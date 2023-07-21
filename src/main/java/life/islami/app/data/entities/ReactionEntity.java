package life.islami.app.data.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import life.islami.app.enums.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "reactions")
public class ReactionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "audio_id", nullable = false)
  private AudioEntity audio;

  @Enumerated(EnumType.STRING)
  private Reaction reaction;

  @Override
  public String toString() {
    return "ReactionEntity [id=" + id + ", user=" + user + ", audio=" + audio + ", reaction=" + reaction + "]";
  }
}
