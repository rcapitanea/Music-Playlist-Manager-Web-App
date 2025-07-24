package ca.sheridancollege.capitanr.beans;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
	
	private long id;
	private String username;
	private String password;
	private List<Song> likedSongs;
	
	public boolean checkSongLiked(long songId) {
		if (likedSongs == null) return false;
		for (Song song : likedSongs) {
			if (song.getId() == songId)
				return true;
		}
		return false;
	}
}
