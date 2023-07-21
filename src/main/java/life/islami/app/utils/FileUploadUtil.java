package life.islami.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {
  public boolean saveFile(String uploadDir, String fileName,
      MultipartFile multipartFile) {

    try {
      Path directoryPath = Paths.get(uploadDir);

      if (!Files.exists(directoryPath)) {
        Files.createDirectories(directoryPath);
        Set<PosixFilePermission> dirPermissions = new HashSet<>();
        dirPermissions.add(PosixFilePermission.GROUP_READ);
        dirPermissions.add(PosixFilePermission.GROUP_WRITE);
        dirPermissions.add(PosixFilePermission.OWNER_READ);
        dirPermissions.add(PosixFilePermission.OWNER_WRITE);
        dirPermissions.add(PosixFilePermission.OTHERS_READ);
        dirPermissions.add(PosixFilePermission.OTHERS_WRITE);
        Files.setPosixFilePermissions(directoryPath, dirPermissions);
      }

      Path objectPath = directoryPath.resolve(fileName);
      InputStream inputStream = multipartFile.getInputStream();
      Files.copy(inputStream, objectPath, StandardCopyOption.REPLACE_EXISTING);
      Set<PosixFilePermission> filePermissions = new HashSet<>();
      filePermissions.add(PosixFilePermission.GROUP_READ);
      filePermissions.add(PosixFilePermission.GROUP_WRITE);
      filePermissions.add(PosixFilePermission.OWNER_READ);
      filePermissions.add(PosixFilePermission.OWNER_WRITE);
      filePermissions.add(PosixFilePermission.OTHERS_READ);
      filePermissions.add(PosixFilePermission.OTHERS_WRITE);
      Files.setPosixFilePermissions(objectPath, filePermissions);

      return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
