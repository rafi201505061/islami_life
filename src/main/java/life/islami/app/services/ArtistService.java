package life.islami.app.services;

import life.islami.app.dtos.ArtistDto;
import life.islami.app.models.request.paramModels.ArtistQueryModel;
import life.islami.app.models.response.PaginatedResponse;

public interface ArtistService {
  ArtistDto createArtist(ArtistDto artistDto);

  PaginatedResponse<ArtistDto> retrieveArtists(ArtistQueryModel artistQueryModel);

  ArtistDto retrieveArtistByArtistId(String artistId);

  ArtistDto updateArtist(ArtistDto artistDto);

}
