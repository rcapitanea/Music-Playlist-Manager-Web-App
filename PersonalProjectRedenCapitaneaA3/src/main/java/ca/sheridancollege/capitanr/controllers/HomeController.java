package ca.sheridancollege.capitanr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.capitanr.beans.Playlist;
import ca.sheridancollege.capitanr.beans.Song;
import ca.sheridancollege.capitanr.beans.User;
import ca.sheridancollege.capitanr.database.DatabaseAccess;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	@Lazy
	DatabaseAccess da;

	@GetMapping("/")
	public String index(HttpSession session, Authentication authentication) {
		if (authentication != null) {
			System.out.println(authentication.getName());
		}
		if (authentication != null && authentication.isAuthenticated())
			session.setAttribute("user", da.findUserAccount(authentication.getName()));
		session.setAttribute("userResult", 0);
		session.setAttribute("otherPlaylists", null);
		session.setAttribute("playlists", null);
		session.setAttribute("selectedPlaylist", null);
		session.setAttribute("song", null);
		session.setAttribute("adminSongObj", null);
		return "index";
	}

	@GetMapping("/register")
	public String register(HttpSession session, Model model) {
		session.setAttribute("userResult", 0);
		if (session.getAttribute("user") != null) {
			return "index";
		}
		session.setAttribute("registration", 1);
		model.addAttribute("user", new User());

		return "user";
	}

	@GetMapping("/login")
	public String login(HttpSession session, Model model) {
		session.setAttribute("userResult", 0);
		if (session.getAttribute("user") != null) {
			return "index";
		}
		session.setAttribute("registration", 0);
		model.addAttribute("user", new User());

		return "user";
	}

	/*
	 * @GetMapping("/logout") public String logout(HttpSession session, Model model)
	 * { session.invalidate();
	 * 
	 * return "index"; }
	 */
	@PostMapping("/register")
	public String registerUser(HttpSession session, Model model, @ModelAttribute User user) {
		if (da.insertUser(user)) {
			/*
			 * User newUser = da.findUserAccount(user.getUsername());
			 * da.addRole(newUser.getId(), Long.valueOf(1)); session.setAttribute("user",
			 * newUser); // we do it this way so that the user id is assigned correctly
			 */		
			session.setAttribute("userResult", 1);
			//return "/secure/user";
		} else {
			session.setAttribute("userResult", 2);
		}
		//model.addAttribute("user", new User());
		return "user";
	}

	/*
	 * @PostMapping("/login") public String loginUser(HttpSession session, Model
	 * model, @RequestParam String username, @RequestParam String password) {
	 * 
	 * List<User> users = da.getUser(username, password); if (users.size() > 0) {
	 * session.setAttribute("user", users.get(0));
	 * session.setAttribute("userResult", 3); return "/secure/index"; } else {
	 * session.setAttribute("userResult", 4); }
	 * 
	 * return "/secure/index"; }
	 */

	@GetMapping("/library")
	public String library(HttpSession session, Authentication authentication) {
		//User user = da.findUserAccount(authentication.getName());
		User user = (User) session.getAttribute("user");
		if (user != null) {

			session.setAttribute("playlistEdit", 0);
			List<Playlist> playlists = da.getUserPlaylists(user);
			session.setAttribute("playlists", playlists);
			session.setAttribute("otherPlaylists", da.getOtherPlaylists(user.getId()));
			session.setAttribute("topHits", null);
			session.setAttribute("selectedPlaylist", null);
			session.setAttribute("adminSongObj", null);
		}
		return "/secure/index";
	}

	@GetMapping("/browse")
	public String browse(HttpSession session) {
		//User user = da.findUserAccount(authentication.getName());
		User user = (User) session.getAttribute("user");
		if (user != null) {
			session.setAttribute("song", null);
			session.setAttribute("playlists", null);
			session.setAttribute("otherPlaylists", null);
			session.setAttribute("selectedPlaylist", null);
			session.setAttribute("topHits", da.getTopHits());
		}
		return "/secure/index";
	}

	@PostMapping("/createPlaylist")
	public String createPlaylist(HttpSession session, @RequestParam String name, @RequestParam String description) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			da.insertPlaylist(user, name, description);
			session.setAttribute("playlists", da.getUserPlaylists(user));
		}
		return "/secure/index";
	}

	@GetMapping("/editPlaylist/{id}")
	public String editPlaylistById(HttpSession session, Model model, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Playlist playlist = da.getUserPlaylistById(id, user.getId());
			if (playlist != null) {
				System.out.println("found playlist");
				model.addAttribute("playlistObj", playlist);
			}
		}
		return "/secure/index";
	}

	@PostMapping("/editPlaylist")
	public String confirmEditPlaylist(HttpSession session, Model model, @RequestParam long id,
			@RequestParam String name, @RequestParam String description) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			da.updatePlaylist(id, name, description);
			session.setAttribute("playlists", da.getUserPlaylists(user));
		}
		return "/secure/index";
	}

	@GetMapping("/deletePlaylist/{id}")
	public String deletePlaylistById(HttpSession session, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			da.deletePlaylist(id, user.getId());
			session.setAttribute("playlists", da.getUserPlaylists(user));
			session.setAttribute("selectedPlaylist", null);
		}
		return "/secure/index";
	}
	
	@GetMapping("/adminDeletePlaylist/{id}")
	public String adminDeletePlaylist(HttpSession session, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			da.forceDeletePlaylist(id);
			session.setAttribute("playlists", da.getUserPlaylists(user));
			session.setAttribute("otherPlaylists", da.getOtherPlaylists(user.getId()));
			session.setAttribute("selectedPlaylist", null);
		}
		return "/secure/index";
	}

	@GetMapping("/addToPlaylist/{id}")
	public String addToPlaylist(HttpSession session, Model model, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Song song = da.getSongById(id);
			if (song != null) {
				session.setAttribute("song", da.getSongById(id));
				session.setAttribute("playlists", da.getUserPlaylists(user));
				session.setAttribute("selectedPlaylist", null);
				session.setAttribute("topHits", null);
			}
		}
		return "/secure/index";
	}

	@GetMapping("/likeSong/{id}")
	public String likeSong(HttpSession session, Model model, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Song song = da.getSongById(id);
			if (song != null) {
				da.userLikeSong(id, user.getId());
				user.setLikedSongs(da.getLikedSongs(user.getId()));

				session.setAttribute("song", null);
				session.setAttribute("playlists", null);
				session.setAttribute("otherPlaylists", null);
				session.setAttribute("selectedPlaylist", null);
				session.setAttribute("topHits", da.getTopHits());
			}
		}

		return "/secure/index";
	}

	@GetMapping("/unlikeSong/{id}")
	public String unlikeSong(HttpSession session, Model model, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Song song = da.getSongById(id);
			if (song != null) {
				da.userUnlikeSong(id, user.getId());
				user.setLikedSongs(da.getLikedSongs(user.getId()));

				session.setAttribute("song", null);
				session.setAttribute("playlists", null);
				session.setAttribute("otherPlaylists", null);
				session.setAttribute("selectedPlaylist", null);
				session.setAttribute("topHits", da.getTopHits());
			}
		}
		return "/secure/index";
	}

	@GetMapping("/unlikeSongFromLib/{id}")
	public String unlikeSongFromLib(HttpSession session, Model model, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			da.userUnlikeSong(id, user.getId());
			user.setLikedSongs(da.getLikedSongs(user.getId()));
		}

		return "/secure/index";
	}

	@PostMapping("/addToPlaylist")
	public String confirmAddToPlaylist(HttpSession session, Model model, @RequestParam long playlistId) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Song song = (Song) session.getAttribute("song");
			if (song != null) {
				da.insertSongToPlaylist(song.getId(), playlistId);
				session.setAttribute("playlists", da.getUserPlaylists(user));
				session.setAttribute("selectedPlaylist", null);
				session.setAttribute("song", null);
			}
		}
		return "/secure/index";
	}

	@GetMapping("/viewPlaylist/{id}")
	public String viewPlaylist(HttpSession session, Model model, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Playlist playlist = da.getPlaylistById(id);
			session.setAttribute("selectedPlaylist", playlist);
		}

		return "/secure/index";
	}

	@GetMapping("/removeFromPlaylist/{id}")
	public String removeFromPlaylist(HttpSession session, @PathVariable long id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Playlist selectedPlaylist = (Playlist) session.getAttribute("selectedPlaylist");
			if (selectedPlaylist != null) {
				da.deleteFromPlaylist(id, selectedPlaylist.getId());
				session.setAttribute("playlists", da.getUserPlaylists(user));
				session.setAttribute("selectedPlaylist", da.getPlaylistById(selectedPlaylist.getId()));
			}
		}
		return "/secure/index";
	}
	
	@GetMapping("/adminEditSong/{id}")
	public String adminEditSong(HttpSession session, Authentication authentication, Model model, @PathVariable long id) {
		Song song = da.getSongById(id);
		if (song != null) {
			session.setAttribute("adminSongObj", song);
		}
		return "/secure/index";
	}
	
	@GetMapping("/adminDeleteSong/{id}")
	public String adminDeleteSong(HttpSession session, Authentication authentication, Model model, @PathVariable long id) {
		da.deleteSong(id);
		session.setAttribute("topHits", da.getTopHits());
		return "/secure/index";
	}
	
	@PostMapping("/adminEditSong")
	public String confirmEditSong(HttpSession session, Authentication authentication, Model model, @RequestParam long id,
			@RequestParam String title, @RequestParam String artist, @RequestParam String album, @RequestParam int duration) {
		da.updateSong(id, title, artist, album, duration);
		session.setAttribute("adminSongObj", null);
		session.setAttribute("topHits", da.getTopHits());
		return "/secure/index";
	}

	
	@GetMapping("/permission-denied")
	public String permissionDenied(HttpSession session) {
		session.setAttribute("userResult", 5);
		return "index";
	}
}
