package guru.qa.niffler.model.allure;

import java.util.List;

public record SendResultsRequest(List<AllureResultFile> results
) {
}