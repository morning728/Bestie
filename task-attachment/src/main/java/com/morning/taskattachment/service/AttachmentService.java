package com.morning.taskattachment.service;

import com.morning.taskattachment.entity.Attachment;
import com.morning.taskattachment.repository.AttachmentRepository;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final MinioClient minioClient;
    private final AttachmentRepository repository;

    private static final String BUCKET = "attachments";
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;

    public Attachment upload(Long taskId, MultipartFile file) throws Exception {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Максимальный размер — 25 МБ");
        }

        String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Проверяем и создаём bucket при необходимости
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
        }

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        Attachment attachment = Attachment.builder()
                .taskId(taskId)
                .filename(file.getOriginalFilename())
                .s3Key(objectName)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(attachment);
    }

    public byte[] download(Long id) throws Exception {
        Attachment attachment = repository.findById(id).orElseThrow();
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET)
                .object(attachment.getS3Key())
                .build())) {
            return stream.readAllBytes();
        }
    }

    public List<Attachment> getAllByTask(Long taskId) {
        return repository.findByTaskId(taskId);
    }

    public void delete(Long id) throws Exception {
        Attachment attachment = repository.findById(id).orElseThrow();
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET)
                .object(attachment.getS3Key())
                .build());
        repository.delete(attachment);
    }
}
