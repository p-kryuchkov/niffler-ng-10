
package guru.qa.niffler.api;

import guru.qa.niffler.model.allure.CreateProjectRequest;
import guru.qa.niffler.model.allure.SendResultsRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AllureApi {

    @POST("/allure-docker-service/projects")
    Call<Void> createProject(@Body CreateProjectRequest request);

    @POST("/allure-docker-service/send-results")
    Call<Void> sendResults(@Body SendResultsRequest request);

    @GET("/allure-docker-service/generate-report")
    Call<Void> generateReport(
            @Query("project_id") String projectId
    );
}