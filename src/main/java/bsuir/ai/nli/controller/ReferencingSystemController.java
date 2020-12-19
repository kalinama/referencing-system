package bsuir.ai.nli.controller;

import bsuir.ai.nli.model.entity.ReferencingResult;
import bsuir.ai.nli.model.service.ReferencingService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ReferencingSystemController {

    @Resource
    private ReferencingService defaultReferencingService;

    @PostMapping("/reference")
    public ModelAndView getReferencingResult(@RequestParam List<MultipartFile> files, Model model) {
        ReferencingResult result = defaultReferencingService.handle(files);
        model.addAttribute("result", result);

        return new ModelAndView("referencingSystem");
    }

    @GetMapping("/")
    public ModelAndView mainPage(){
        return new ModelAndView("referencingSystem");
    }
}
