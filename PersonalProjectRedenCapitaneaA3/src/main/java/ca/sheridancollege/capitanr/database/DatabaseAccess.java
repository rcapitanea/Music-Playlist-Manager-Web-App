package ca.sheridancollege.capitanr.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.capitanr.beans.Playlist;
import ca.sheridancollege.capitanr.beans.Song;
import ca.sheridancollege.capitanr.beans.User;


@Repository
public class DatabaseAccess {

	@Autowired
	protected NamedParameterJdbcTemplate jdbc;
	

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public boolean insertUser(User user) {
		if (checkUsername(user.getUsername()))
		{
			return false;
		}
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO users(username, password) VALUES (:username, :password)";
		namedParameters.addValue("username", user.getUsername());
		namedParameters.addValue("password", passwordEncoder.encode(user.getPassword()));
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) {
			System.out.println("User inserted into database");
			return true;
		} else {
			return false;
		}
	}
	
	public boolean insertPlaylist(User user, String name, String description) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO playlist(name, description, creator_id, creator_name) VALUES (:name, :description, :creator_id, :creator_name)";
		namedParameters.addValue("name", name);
		namedParameters.addValue("description", description);
		namedParameters.addValue("creator_id", user.getId());
		namedParameters.addValue("creator_name", user.getUsername());
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) {
			System.out.println("Playlist inserted into database");
			return true;
		}
		return false;
		
	}
	
	public boolean insertSongToPlaylist(long songId, long playlistId) {
		if (checkSongInPlaylist(songId, playlistId)) {
			System.out.println("Song already exists in this playlist.");
			return false;
		}
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO playlist_song(playlist_id, song_id) VALUES (:playlist_id, :song_id)";
		namedParameters.addValue("playlist_id", playlistId);
		namedParameters.addValue("song_id", songId);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) {
			System.out.println("Song inserted into playlist");
			return true;
		}
		return false;
	}
	
	public boolean userLikeSong(long songId, long userId) {
		if (checkLikedSongs(songId, userId)) {
			System.out.println("Song is already liked by user.");
			return false;
		}
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO liked_songs(user_id, song_id) VALUES (:user_id, :song_id)";
		namedParameters.addValue("user_id", userId);
		namedParameters.addValue("song_id", songId);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) {
			System.out.println("Song inserted into liked_songs");
			return true;
		}
		return false;
	}
	
	public void userUnlikeSong(long songId, long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM liked_songs WHERE song_id=:song_id AND user_id=:user_id";
		namedParameters.addValue("song_id", songId);
		namedParameters.addValue("user_id", userId);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) 
			System.out.println("Song deleted from liked_songs");
	}
	
	
	
	
	public List<Song> getLikedSongs(long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT s.id, s.title, s.artist_name, s.album_name, s.duration, s.rank " +
                "FROM song s " +
                "JOIN liked_songs ps ON s.id = ps.song_id " +
                "WHERE ps.user_id = :user_id";
		namedParameters.addValue("user_id", userId);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Song>(Song.class));
	}
	
	public boolean updatePlaylist(long id, String name, String description) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "UPDATE playlist SET name=:name, description=:description WHERE id=:id";
		namedParameters.addValue("name", name);
		namedParameters.addValue("description", description);
		namedParameters.addValue("id", id);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) {
			System.out.println("Playlist updated into database");
			return true;
		}
		return false;
		
	}
	
	public boolean checkSongInPlaylist(long songId, long playlistId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT COUNT(*) FROM playlist_song WHERE playlist_id = :playlist_id AND song_id = :song_id";
		namedParameters.addValue("playlist_id", playlistId);
		namedParameters.addValue("song_id", songId);
		return jdbc.queryForObject(query, namedParameters, Integer.class) > 0;
	}
	
	
	public boolean checkLikedSongs(long songId, long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT COUNT(*) FROM liked_songs WHERE user_id = :user_id AND song_id = :song_id";
		namedParameters.addValue("user_id", userId);
		namedParameters.addValue("song_id", songId);
		return jdbc.queryForObject(query, namedParameters, Integer.class) > 0;
	}
	
	public void deletePlaylist(long id, long creatorId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM playlist WHERE id=:id AND creator_id=:creator_id";
		namedParameters.addValue("id", id);
		namedParameters.addValue("creator_id", creatorId);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) 
			System.out.println("Playlist deleted from database");
	}
	
	public void forceDeletePlaylist(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM playlist WHERE id=:id";
		namedParameters.addValue("id", id);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) 
			System.out.println("Playlist deleted from database");
	}
	
	
	public boolean checkUsername(String username) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT COUNT(*) FROM users WHERE username = :username";
		namedParameters.addValue("username", username);
		return jdbc.queryForObject(query, namedParameters, Integer.class) > 0;
	}
	
	public String getCreatorNameById(long creatorId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT username FROM users WHERE id = :creatorId";
		namedParameters.addValue("id", creatorId);
		return jdbc.queryForObject(query, namedParameters, String.class);
	}
	
	public List<User> getUser(String username, String password) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM users WHERE username = :username AND password = :password";
		namedParameters.addValue("username", username);
		namedParameters.addValue("password", password);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<User>(User.class));
	}
	
	public User findUserAccount(String username) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM users WHERE username = :username";
		namedParameters.addValue("username", username);
		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<>(User.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}
	
	public List<Playlist> getUserPlaylists(User user) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM playlist WHERE creator_id = :userid";
		namedParameters.addValue("userid", user.getId());
		List<Playlist> playlists = jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Playlist>(Playlist.class));
		for (Playlist playlist : playlists) {
			playlist.setSongList(getPlaylistSongs(playlist.getId()));
		}
		return playlists;
	}
	
	public List<Playlist> getOtherPlaylists(long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM playlist WHERE creator_id != :userid";
		namedParameters.addValue("userid", userId);
		List<Playlist> playlists = jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Playlist>(Playlist.class));
		for (Playlist playlist : playlists) {
			playlist.setSongList(getPlaylistSongs(playlist.getId()));
		}
		return playlists;
	}
	
	public Playlist getUserPlaylistById(long id, long creatorId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM playlist WHERE id = :id AND creator_id = :creator_id";
		namedParameters.addValue("id", id);
		namedParameters.addValue("creator_id", creatorId);
		Playlist playlist = jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Playlist>(Playlist.class)).get(0);
		if (playlist != null)
			playlist.setSongList(getPlaylistSongs(playlist.getId()));
		return playlist;
	}
	
	public Playlist getPlaylistById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM playlist WHERE id = :id";
		namedParameters.addValue("id", id);
		Playlist playlist = jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Playlist>(Playlist.class)).get(0);
		if (playlist != null)
			playlist.setSongList(getPlaylistSongs(playlist.getId()));
		return playlist;
	}
	
	public List<Song> getPlaylistSongs(long playlistId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT s.id, s.title, s.artist_name, s.album_name, s.duration, s.rank " +
                "FROM song s " +
                "JOIN playlist_song ps ON s.id = ps.song_id " +
                "WHERE ps.playlist_id = :playlist_id";
		namedParameters.addValue("playlist_id", playlistId);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Song>(Song.class));
	}
	
	public List<Song> getTopHits() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM song";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Song>(Song.class));
	}
	
	
	public Song getSongById(long id) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM song WHERE id=:id";
		namedParameters.addValue("id", id);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Song>(Song.class)).get(0);
	}
	
	public void deleteFromPlaylist(long songId, long playlistId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM playlist_song WHERE song_id=:song_id AND playlist_id=:playlist_id";
		namedParameters.addValue("song_id", songId);
		namedParameters.addValue("playlist_id", playlistId);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) 
			System.out.println("Song deleted from playlist");
	}
	
	public List<String> getRolesById(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT sec_role.roleName "
				+ "FROM user_role, sec_role "
				+ "WHERE user_role.roleId = sec_role.roleId "
				+ "AND userId = :userId";
		namedParameters.addValue("userId", userId);
		return jdbc.queryForList(query, namedParameters, String.class);
	}
	
	public void addRole(Long userId, Long roleId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO user_role (userId, roleId) "
				+ "VALUES (:userId, :roleId)";
		namedParameters.addValue("userId", userId);
		namedParameters.addValue("roleId", roleId);
		jdbc.update(query, namedParameters);
	}
	
	public boolean updateSong(long id, String title, String artistName, String albumName, int duration) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "UPDATE song SET title=:title, artist_name=:artistName, album_name=:albumName, duration=:duration WHERE id=:id";
		namedParameters.addValue("title", title);
		namedParameters.addValue("artistName", artistName);
		if (albumName == "")
			albumName = null;
		namedParameters.addValue("albumName", albumName);
		namedParameters.addValue("duration", duration);
		namedParameters.addValue("id", id);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) {
			System.out.println("Song updated into database");
			return true;
		}
		return false;
		
	}
	
	public void deleteSong(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM song WHERE id=:id";
		namedParameters.addValue("id", id);
		int rowsAffected = jdbc.update(query, namedParameters);
		if (rowsAffected > 0) 
			System.out.println("Song deleted from database");
	}
}
