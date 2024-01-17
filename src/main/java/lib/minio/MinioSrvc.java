package lib.minio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tujuhsembilan.bookrecipe.dto.request.CreateRecipeRequest;
import com.tujuhsembilan.bookrecipe.dto.request.UpdateRecipeRequest;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lib.i18n.utility.MessageUtil;
import lib.minio.configuration.property.MinioProp;
import lib.minio.exception.MinioServiceDownloadException;
import lib.minio.exception.MinioServiceUploadException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MinioSrvc {

  public static final Long DEFAULT_EXPIRY = TimeUnit.HOURS.toSeconds(1);

  private final MinioClient minio;
  private final MinioProp prop;

  private final MessageUtil message;

  private static String bMsg(String bucket) {
    return "bucket " + bucket;
  }

  private static String bfMsg(String bucket, String filename) {
    return bMsg(bucket) + " of file " + filename;
  }

  public String getLink(String filename, Long expiry) {
    try {
      return minio.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(prop.getBucketName())
              .object(filename)
              .expiry(Math.toIntExact(expiry), TimeUnit.SECONDS)
              .build());
    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
        | IllegalArgumentException | IOException e) {
      log.error(message.get(prop.getGetErrorMessage(), bfMsg(prop.getBucketName(), filename)) + ": " + e.getLocalizedMessage(), e);
      throw new MinioServiceDownloadException(
          message.get(prop.getGetErrorMessage(), bfMsg(prop.getBucketName(), filename)), e);
    }
  }
  
  public String getPublicLink(String filename) {
	  return this.getLink(filename, DEFAULT_EXPIRY);
  }

  @Data
  public static class ListItem {
    private String objectName;
    private Long size;
    private boolean dir;
    private String versionId;

    @JsonIgnore
    private Item item;

    public ListItem(Item item) {
      this.objectName = item.objectName();
      this.size = item.size();
      this.dir = item.isDir();
      this.versionId = item.versionId();
      this.item = item;
    }
  }

  public List<Object> getList(String bucket) {
    List<Result<Item>> results = new ArrayList<>();
    minio.listObjects(
        ListObjectsArgs.builder()
            .bucket(bucket)
            .recursive(true)
            .build())
        .forEach(results::add);
    return results.stream().map(t -> {
      try {
        return new ListItem(t.get());
      } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException
          | InternalException | InvalidResponseException | NoSuchAlgorithmException | ServerException
          | XmlParserException | IOException e) {
        log.error(message.get(prop.getGetErrorMessage(), bMsg(bucket)) + ": " + e.getLocalizedMessage(), e);
        return null;
      }
    }).collect(Collectors.toList());
  }

  public void view(HttpServletResponse response, String filename, Long expiry) {
    try {
      response.sendRedirect(this.getLink(filename, expiry));
    } catch (IOException e) {
      log.error(message.get(prop.getGetErrorMessage(), bfMsg(prop.getBucketName(), filename)) + ": " + e.getLocalizedMessage(), e);
      throw new MinioServiceDownloadException(
          message.get(prop.getGetErrorMessage(), bfMsg(prop.getBucketName(), filename)), e);
    }
  }

  public void view(HttpServletResponse response, String filename) {
    this.view(response, filename, DEFAULT_EXPIRY);
  }

  @Data
  @Builder
  public static class UploadOption {
    private String filename;
  }

  public ObjectWriteResponse upload(MultipartFile file, Function<MultipartFile, UploadOption> modifier) {
    UploadOption opt = modifier.apply(file);
    try {
      return minio.putObject(
          PutObjectArgs.builder()
              .bucket(prop.getBucketName())
              .object(opt.filename)
              .stream(file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build());
    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
        | IllegalArgumentException | IOException e) {
      log.error(message.get(prop.getPostErrorMessage(), bfMsg(prop.getBucketName(), opt.filename)) + ": " + e.getLocalizedMessage(),
          e);
      throw new MinioServiceUploadException(
          message.get(prop.getPostErrorMessage(), prop.getBucketName(), opt.filename), e);
    }
  }

  private String sanitizeForFilename(String input) {
    return input.replaceAll("[^a-zA-Z0-9]", "_");
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex == -1) ? "" : filename.substring(dotIndex);
  }

  public String uploadImageToMinio(CreateRecipeRequest request, MultipartFile imageFile) throws IOException {
        String recipeName = sanitizeForFilename(request.getRecipeName());
        String categoryName = sanitizeForFilename(request.getCategories().getCategoryName());
        String levelName = sanitizeForFilename(request.getLevels().getLevelName());

        if (recipeName.isEmpty() || categoryName.isEmpty() || levelName.isEmpty()) {
            log.warn("One or more components for filename are empty. Recipe: {}, Category: {}, Level: {}",
                    request.getRecipeName(), request.getCategories().getCategoryName(),
                    request.getLevels().getLevelName());
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = getFileExtension(imageFile.getOriginalFilename());

        String generatedFilename = String.format(
                "%s_%s_%s_%s%s",
                recipeName,
                categoryName,
                levelName,
                timestamp,
                fileExtension);

        try (InputStream inputStream = imageFile.getInputStream()) {
            minio.putObject(
                    PutObjectArgs.builder()
                            .bucket(prop.getBucketName())
                            .object(generatedFilename)
                            .stream(inputStream, imageFile.getSize(), -1)
                            .contentType(imageFile.getContentType())
                            .build());
        } catch (Exception e) {
            throw new IOException("Failed to upload image to MinIO", e);
        }

        log.info(generatedFilename);
        return generatedFilename;
    }

    public String updateImageToMinio(UpdateRecipeRequest request, MultipartFile imageFile) throws IOException {
        String recipeName = sanitizeForFilename(request.getRecipeName());
        String categoryName = sanitizeForFilename(request.getCategories().getCategoryName());
        String levelName = sanitizeForFilename(request.getLevels().getLevelName());

        if (recipeName.isEmpty() || categoryName.isEmpty() || levelName.isEmpty()) {
            log.warn("One or more components for filename are empty. Recipe: {}, Category: {}, Level: {}",
                    request.getRecipeName(), request.getCategories().getCategoryName(),
                    request.getLevels().getLevelName());
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = getFileExtension(imageFile.getOriginalFilename());

        String generatedFilename = String.format(
                "%s_%s_%s_%s%s",
                recipeName,
                categoryName,
                levelName,
                timestamp,
                fileExtension);

        try (InputStream inputStream = imageFile.getInputStream()) {
            minio.putObject(
                    PutObjectArgs.builder()
                            .bucket(prop.getBucketName())
                            .object(generatedFilename)
                            .stream(inputStream, imageFile.getSize(), -1)
                            .contentType(imageFile.getContentType())
                            .build());
        } catch (Exception e) {
            throw new IOException("Failed to upload image to MinIO", e);
        }

        log.info(generatedFilename);
        return generatedFilename;
    }

  public ObjectWriteResponse upload(MultipartFile file) {
    return this.upload(file,
        o -> UploadOption.builder()
            .filename(System.currentTimeMillis() + "_-_"
                + o.getOriginalFilename().replace(" ", "_"))
            .build());
  }

  // ---

  public ObjectWriteResponse upload(InputStream file, String filename) {
    try {
      return minio.putObject(
          PutObjectArgs.builder()
              .bucket(prop.getBucketName())
              .object(filename)
              .stream(file, file.available(), -1)
              .build());
    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
        | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
        | IllegalArgumentException | IOException e) {
      log.error(message.get(prop.getPostErrorMessage(), bfMsg(prop.getBucketName(), filename)) + ": " + e.getLocalizedMessage(),
          e);
      throw new MinioServiceUploadException(
          message.get(prop.getPostErrorMessage(), prop.getBucketName(), filename), e);
    }
  }

  public InputStream read(String filename) throws InvalidKeyException, ErrorResponseException,
      InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException,
      XmlParserException, IllegalArgumentException, IOException {
    return minio.getObject(GetObjectArgs.builder()
        .bucket(prop.getBucketName())
        .object(filename)
        .build());
  }

}
