package life.islami.app.data.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import life.islami.app.enums.AudioGenre;
import life.islami.app.enums.SongApprovalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audio")
public class AudioEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, unique = true, length = 12)
  private String audioId;

  @Column(nullable = false, length = 500)
  private String title;

  @Column(length = 2000)
  private String description;

  @Column(nullable = false)
  private String imageUrl;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AudioGenre genre;

  @Column(nullable = false)
  private boolean deleted = false;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SongApprovalStatus approvalStatus = SongApprovalStatus.IN_REVIEW;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date creationTime;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date approvalTime;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Date lastUpdateTime;

  @Column(nullable = false)
  private String creatorId;

  private String checkedBy = null;

  private String summary = null;

  @ManyToOne
  @JoinColumn(name = "artist_id")
  private ArtistEntity artist;

  @Column(nullable = false)
  private long likeCount = 0;

  @Column(nullable = false)
  private long dislikeCount = 0;

  @Column(nullable = false)
  private long numComments = 0;

  @Override
  public String toString() {
    return "AudioEntity [id=" + id + ", audioId=" + audioId + ", title=" + title + ", description=" + description
        + ", imageUrl=" + imageUrl + ", url=" + url + ", genre=" + genre + ", deleted=" + deleted + ", approvalStatus="
        + approvalStatus + ", creationTime=" + creationTime + ", approvalTime=" + approvalTime + ", lastUpdateTime="
        + lastUpdateTime + ", creatorId=" + creatorId + ", checkedBy=" + checkedBy + ", summary=" + summary
        + ", artist=" + artist + ", likeCount=" + likeCount + ", dislikeCount=" + dislikeCount + ", numComments="
        + numComments + "]";
  }
}
