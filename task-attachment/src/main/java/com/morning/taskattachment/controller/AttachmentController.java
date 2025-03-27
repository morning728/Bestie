package com.morning.taskattachment.controller;

import com.morning.taskattachment.entity.Attachment;
import com.morning.taskattachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Attachment>> getByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(service.getAllByTask(taskId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        byte[] data = service.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

