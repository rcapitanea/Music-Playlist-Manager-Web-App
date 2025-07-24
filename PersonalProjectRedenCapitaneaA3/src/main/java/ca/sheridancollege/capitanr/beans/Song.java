package ca.sheridancollege.capitanr.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Song {
	
	private long id;
	private String title;
	private String artistName;
	private String albumName;
	private int duration;
	private int rank;
	
	public String getFormattedDuration() {
		int minutes = duration / 60;
		int seconds = duration % 60;
		
		return String.format("%d:%02d",minutes, seconds);
	}
	
}
