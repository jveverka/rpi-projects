package one.microproject.devicecontroller.config;

import one.microproject.devicecontroller.config.security.IAMSecurityFilterConfiguration;
import one.microproject.iamservice.client.IAMClient;
import one.microproject.iamservice.client.IAMClientBuilder;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix="app.iam-client")
public class IAMClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(IAMClientConfiguration.class);

    private String organizationId;
    private String projectId;
    private String baseUrl;
    private Long pollingInterval;
    private TimeUnit timeUnit;

    @PostConstruct
    public void init() {
        LOG.info("## organizationId={}", organizationId);
        LOG.info("## projectId={}", projectId);
        LOG.info("## baseUrl={}", baseUrl);
        LOG.info("## pollingInterval={}", pollingInterval);
        LOG.info("## timeUnit={}", timeUnit);
        LOG.info("#CONFIG: initializing Bouncy Castle Provider (BCP) ...");
        Security.addProvider(new BouncyCastleProvider());
    }

    @Bean
    @Scope("singleton")
    public IAMSecurityFilterConfiguration createIAMSecurityFilterConfiguration() throws MalformedURLException {
        LOG.info("createIAMClient");
        IAMClient iamClient = IAMClientBuilder.builder()
                .setOrganizationId(organizationId)
                .setProjectId(projectId)
                .withHttpProxy(new URL(baseUrl), pollingInterval, timeUnit)
                .build();
        return new IAMSecurityFilterConfiguration(iamClient, Set.of("/api/system/**", "/actuator/**"));
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setPollingInterval(Long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public OrganizationId getOrganizationId()  {
        return OrganizationId.from(organizationId);
    }

    public ProjectId getProjectId() {
        return ProjectId.from(projectId);
    }

}
