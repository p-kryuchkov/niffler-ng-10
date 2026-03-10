package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.service.AllureApiClient;

public class AllureDockerExtension implements SuiteExtension{
    private static final AllureApiClient allureApiClient = new AllureApiClient();
    @Override
    public void afterSuite() {
        allureApiClient.createProject();
        allureApiClient.sendResults();
        allureApiClient.generateReport();
    }
}
