package codes.fepi.core;

import codes.fepi.global.Properties;
import codes.fepi.google.DriveAuth;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

public class DriveUpload {
	public static void uploadDir(Path dir, String folderId) {
		try {
			Drive service = DriveAuth.getService();
			Files.walkFileTree(Properties.getOutputPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					uploadSingle(file, folderId, service);
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void uploadSingle(Path file, String folderId, Drive service) {
		try {
			File fileMetadata = new File()
					.setName(file.getFileName().toString())
					.setParents(Collections.singletonList(folderId));
			service.files().create(fileMetadata,
					new FileContent("audio/mp3", file.toFile()))
					.setFields("parents")
					.execute();
			System.out.println("uploaded " + file.getFileName().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
