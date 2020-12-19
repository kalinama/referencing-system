package bsuir.ai.nli.model.service.impl;

import bsuir.ai.nli.model.entity.ReferencingResult;
import bsuir.ai.nli.model.service.ProcessingService;
import bsuir.ai.nli.model.service.ReferencingService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultReferencingService implements ReferencingService {
    @Resource
    private ProcessingService MLProcessingService;
    @Resource
    private ProcessingService classicProcessingService;

    public ReferencingResult handle(List<MultipartFile> fileList){
        List<String> allContext = fileList.stream().map(file -> {
            try {
                return new String(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }).collect(Collectors.toList());

        return ReferencingResult.builder()
                .snippet(classicProcessingService.process(allContext))
                .snippetML(MLProcessingService.process(allContext))
                .documentContent(String.join("", allContext))
                .build();
    }
}
