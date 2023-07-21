package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import life.islami.app.enums.PrivacyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PlaylistCreationRequestModel extends PlaylistUpdateRequestModel {
  @NotNull(message = "Playlist name required")
  @Size(max = 250, message = "Playlist name can not be more than 250 characters long")
  @NotBlank(message = "Playlist name can not be blank")
  private String playlistName;

  private String description;

  @NotNull(message = "Privacy type required [PRIVATE, PUBLIC]")
  private PrivacyType privacyType;
  @NotNull(message = "Owner id required")
  @NotBlank(message = "Owner id can not be blank")
  private String ownerId;
}
