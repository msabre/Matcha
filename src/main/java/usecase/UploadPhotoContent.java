package usecase;

import application.services.MatchUtils;
import config.MyProperties;
import domain.entity.Photo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class UploadPhotoContent {
    public void upload(List<Photo> photos) {
        for (Photo photo : photos) {
            if (photo == null)
                continue;

            String path = String.format("%sIMG_%s_photo_%s.%s", MyProperties.IMAGES_PATH + MatchUtils.getSlash(), photo.getUserId(), photo.getNumber(), photo.getFormat());
            File file =  new File(path);
            writeContent(photo, file, path);
        }

        Photo photo = photos.get(5);
        uploadMain(photo);
    }

    public void uploadMain(Photo photo) {
        if (photo == null)
            return;
        String path = String.format("%sIMG_MAIN_%s_photo_%s.%s", MyProperties.IMAGES_PATH + MatchUtils.getSlash(), photo.getUserId(), photo.getNumber(), photo.getFormat());
        File file =  new File(path);
        writeContent(photo, file, path);
    }

    private void writeContent(Photo photo, File file, String path) {
        if (file.exists()) {
            try {
                byte[] content = Files.readAllBytes(Paths.get(path));
                photo.setContent(new String(Base64.getEncoder().encode(content), StandardCharsets.UTF_8));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
