package com.box.bc.boxconnectiontest;

import com.box.sdk.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.*;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;

@Component
@Slf4j
@Command(name = "connect")
public class ConnectionCommand implements Callable<Integer> {

    @Option(names = "--config-file", description = "Box JWT Config", required = true)
    String configFile;

    @Option(names = "--user-id", description = "User ID for Root Folder Query")
    String userId;

    @Option(names = "--file-info-id", description = "File Info ID. Needs User ID to be defined")
    String fileId;

    public Integer call() throws Exception {
        Reader reader = new FileReader(configFile);
        BoxConfig boxConfig = BoxConfig.readFrom(reader);
        BoxDeveloperEditionAPIConnection api = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
        InMemoryLRUAccessTokenCache accessTokenCache = new InMemoryLRUAccessTokenCache(10);
        log.info("Service Account Token: {}", api.getAccessToken());
        log.info("Service Account User: {}", BoxUser.getCurrentUser(api).getInfo().getLogin());
        if (!StringUtils.isEmpty(userId)) {
            BoxDeveloperEditionAPIConnection apiuser = BoxDeveloperEditionAPIConnection.getUserConnection(userId, boxConfig,accessTokenCache);
            log.info("User {} Account Token: {}",userId, apiuser.getAccessToken());
            log.info("Account User {}: {}", userId, BoxUser.getCurrentUser(apiuser).getInfo().getLogin());

            BoxFolder folder = new BoxFolder(apiuser, "0");
            for (BoxItem.Info itemInfo : folder) {
                if (itemInfo instanceof BoxFile.Info) {
                    BoxFile.Info fileInfo = (BoxFile.Info) itemInfo;
                    log.info("File {} - {}", fileInfo.getID(),fileInfo.getName());
                } else if (itemInfo instanceof BoxFolder.Info) {
                    BoxFolder.Info folderInfo = (BoxFolder.Info) itemInfo;
                    log.info("Folder {} - {}", folderInfo.getID(),folderInfo.getName());
                }
            }
        }

        if (!StringUtils.isEmpty(fileId)) {
            BoxDeveloperEditionAPIConnection apiuser = BoxDeveloperEditionAPIConnection.getUserConnection(userId, boxConfig,accessTokenCache);
            log.info("Account User {}: {}", userId, BoxUser.getCurrentUser(apiuser).getInfo().getLogin());
            BoxFile file = new BoxFile(apiuser,fileId);
            log.info("File Information {} - {}", file.getInfo().getID(),file.getInfo().getName());
        }
        return 0;
    }
}