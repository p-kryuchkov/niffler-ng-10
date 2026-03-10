package guru.qa.niffler.service;

import guru.qa.niffler.api.AllureApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.allure.AllureResultFile;
import guru.qa.niffler.model.allure.CreateProjectRequest;
import guru.qa.niffler.model.allure.SendResultsRequest;
import guru.qa.niffler.utils.AllureReportUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static ch.qos.logback.core.util.OptionHelper.getEnv;
import static guru.qa.niffler.utils.AllureReportUtils.readAllureResultFiles;

public class AllureApiClient extends RestClient {
    private final AllureApi allureApi;
    private static final String allureBaseUrl = getEnv("ALLURE_DOCKER_API");
    private static final String PROJECT_ID = "Niffler";

    public AllureApiClient() {
        super(allureBaseUrl);
        this.allureApi = create(AllureApi.class);
    }

    public void createProject() {
        CreateProjectRequest request = new CreateProjectRequest(PROJECT_ID);

        try {
            Response<Void> response = allureApi.createProject(request).execute();

            if (response.code() != 200) {
                throw new IllegalStateException(
                        "Failed to create allure project. Code: "
                                + response.code()
                                + ", error: " + getErrorBody(response)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create allure project", e);
        }
    }

    public void sendResults() {
        List<AllureResultFile> resultFiles = readAllureResultFiles();
        SendResultsRequest request = new SendResultsRequest(PROJECT_ID, resultFiles, true);

        try {
            Response<Void> response = allureApi.sendResults(request).execute();

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                        "Failed to send allure results. Code: "
                                + response.code()
                                + ", error: " + getErrorBody(response)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send allure results", e);
        }
    }

    public void generateReport() {
        try {
            Response<Void> response = allureApi.generateReport(PROJECT_ID).execute();

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                        "Failed to generate allure report. Code: "
                                + response.code()
                                + ", error: " + getErrorBody(response)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate allure report", e);
        }
    }

    private static String getErrorBody(Response<?> response) {
        try {
            return response.errorBody() == null ? "" : response.errorBody().string();
        } catch (IOException e) {
            return "Unable to read error body";
        }
    }
}
