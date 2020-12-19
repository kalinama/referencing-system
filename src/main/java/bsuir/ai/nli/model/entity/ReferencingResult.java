package bsuir.ai.nli.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ReferencingResult {
    private String snippet; //result of classic referencing
    private String snippetML; //result of ML referencing
    private String documentContent; //initial document content
}
