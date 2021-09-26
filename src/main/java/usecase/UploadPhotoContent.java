package usecase;

import application.services.MatchUtils;
import config.MyProperties;
import domain.entity.Photo;
import usecase.port.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class UploadPhotoContent {
    public void upload(Collection<Photo> photos) {
        for (Photo photo : photos) {
            if (photo == null)
                continue;
            String path = String.format("%sIMG_%s_%s_photo.jpg", MyProperties.IMAGES_PATH + MatchUtils.getSlash(), photo.getUserId(), photo.getNumber());
            File file =  new File(path);

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
}
