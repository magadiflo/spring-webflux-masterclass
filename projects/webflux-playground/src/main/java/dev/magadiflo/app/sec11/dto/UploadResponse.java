package dev.magadiflo.app.sec11.dto;

import java.util.UUID;

public record UploadResponse(UUID confirmationId,
                             Long productCount) {
}
