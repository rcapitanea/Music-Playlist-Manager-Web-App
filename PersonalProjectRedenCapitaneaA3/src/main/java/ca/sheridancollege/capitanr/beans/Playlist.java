package ca.sheridancollege.capitanr.beans;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Playlist {
	
	private long id;
	private long creatorId;
	private String creatorName;
	private String name;
	private List<Song> songList;
	private String description;
	
	public int getTotalPlaylistDuration() {
		if (songList == null) return 0;
		int tDuration = 0;
		for (Song song : songList) {
			tDuration += song.getDuration();
		}
		return tDuration;
	}
	
	public String getFormattedPlaylistDuration() {
		int totalDuration = getTotalPlaylistDuration();
		int minutes = totalDuration / 60;
		int seconds = totalDuration % 60;

		return String.format("%d:%02d",minutes, seconds);
	}
}
