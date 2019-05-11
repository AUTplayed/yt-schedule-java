package codes.fepi;

import codes.fepi.core.*;
import codes.fepi.entities.Video;
import codes.fepi.global.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	private static String PLAYLIST_URI = "";
	private static String PARENT_FOLDER_ID = "";

	public static void main(String args[]) throws Exception {
		CommandLine cli = new DefaultParser().parse(new Options()
						.addRequiredOption("u", "uri", true, "playlist uri to download")
						.addRequiredOption("p", "parent", true, "parent folder id"),
				args);

		PLAYLIST_URI = cli.getOptionValue("uri");
		PARENT_FOLDER_ID = cli.getOptionValue("parent");

		deleteDir(Properties.getOutputPath());
		Properties.getYtdlPath().toFile().delete();

		LibraryUpdater.updateYTDL((out) -> {
			if (out != null) out.printStackTrace();
			Properties.getYtdlPath().toFile().setExecutable(true);
			YTDL.checkPlaylist(URI.create(PLAYLIST_URI),
					(playlistVideos) -> {
						List<Video> toDownload = playlistVideos.stream().filter(Video::isDownload).collect(Collectors.toList());
						List<Video> succVideos = new ArrayList<>(toDownload.size());
						YTDL.downloadVideos(toDownload, AudioFormat.mp3, (video, exception) -> {
									if (exception != null) {
										exception.printStackTrace();
									} else {
										System.out.println("downloaded " + video.getTitle());
										succVideos.add(video);
									}
								},
								() -> finishedDownload(succVideos));
					}, Throwable::printStackTrace);
		});

	}

	private static void deleteDir(Path directory) throws IOException {
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void finishedDownload(List<Video> succVideos) {
		try {
			// cleanup .temp files
			File[] files = Properties.getOutputPath().toFile().listFiles((dir, name) -> name.endsWith(".temp"));
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			DriveUpload.uploadDir(Properties.getOutputPath(), PARENT_FOLDER_ID);
			// update playlist file
			PlaylistStatus.updateDownloadedVideos(succVideos);
			System.out.println("finished");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
