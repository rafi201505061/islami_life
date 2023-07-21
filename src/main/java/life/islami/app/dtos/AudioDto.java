package life.islami.app.dtos;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import life.islami.app.enums.AudioGenre;
import life.islami.app.enums.SongApprovalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class AudioDto {
  private String audioId;
  private String title;
  private String description;
  private MultipartFile audio;
  private MultipartFile image;
  private String imageUrl;
  private String url;
  private AudioGenre genre;
  private Date creationTime;
  private String summary = null;
  private ArtistDto artist;
  private String creatorId;
  private String checkedBy;
  private SongApprovalStatus approvalStatus = SongApprovalStatus.IN_REVIEW;
  private long likeCount = 0;
  private long dislikeCount = 0;
  private long numComments = 0;

}
