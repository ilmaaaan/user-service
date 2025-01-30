package habittracker.userservice.actuator.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomInfoActuator implements InfoContributor {

    private final Environment environment;
    private final BuildProperties buildProperties;
    private final MetricsEndpoint metricsEndpoint;
    private final GitProperties gitProperties;

    public CustomInfoActuator(Environment environment, BuildProperties buildProperties,
                              @Autowired(required = false) MetricsEndpoint metricsEndpoint,
                              GitProperties gitProperties) {
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.metricsEndpoint = metricsEndpoint;
        this.gitProperties = gitProperties;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> mainInfo = new HashMap<>();
        Map<String, Object> metrics = new HashMap<>();
        mainInfo.put("applicationName", buildProperties.getName());
        mainInfo.put("artifactId", buildProperties.getArtifact());
        mainInfo.put("launchTime", buildProperties.getTime());
        mainInfo.put("appVersion", buildProperties.getVersion());
        mainInfo.put("contextPath", environment.getProperty("server.servlet.context-path", "/"));
        mainInfo.put("activeProfiles", environment.getActiveProfiles());
        builder.withDetail("mainInfo", mainInfo);
        metrics.put("uptime", metricsEndpoint.metric("process.uptime", null)
                .getMeasurements().get(0).getValue());
        metrics.put("cpuUsage", metricsEndpoint.metric("process.cpu.usage", null)
                .getMeasurements().get(0).getValue());
        metrics.put("memoryUsage", metricsEndpoint.metric("jvm.memory.used", null)
                .getMeasurements().get(0).getValue());
        metrics.put("buildVersion", "??????");               // на будущее...
        metrics.put("currentBranch", gitProperties.getBranch());
        metrics.put("lastCommit", gitProperties.getCommitId());
        builder.withDetail("metrics", metrics);
    }
}
