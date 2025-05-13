package com.morning.taskattachment.controller;

import com.morning.taskattachment.entity.Attachment;
import com.morning.taskattachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/file-api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService service;

    @PostMapping("/{taskId}")
    public ResponseEntity<Attachment> upload(
            @PathVariable Long taskId,
            @RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.upload(taskId, file));
    }

    @PostMapping("/reports/{projectId}")
    public ResponseEntity<Attachment> uploadReport(
            @PathVariable Long projectId,
            @RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.uploadReport(projectId, file));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Attachment>> getByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(service.getAllByTask(taskId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Attachment>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getAllByProject(projectId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        Attachment attachment = service.getMetadata(id); // создаем такой метод ниже
        byte[] data = service.download(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(attachment.getFilename(), StandardCharsets.UTF_8) + "\"")
                .body(data);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

