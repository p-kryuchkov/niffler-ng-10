package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SendResultsRequest(String project_id,
                                 List<AllureResultFile> results,
                                 @JsonProperty("force_project_creation") boolean forceProjectCreation) {
}