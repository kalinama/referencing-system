package bsuir.ai.nli.model.service;

import bsuir.ai.nli.model.entity.ReferencingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReferencingService {
    ReferencingResult handle(List<MultipartFile> fileList);
}
