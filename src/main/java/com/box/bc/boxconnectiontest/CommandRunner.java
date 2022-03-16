package com.box.bc.boxconnectiontest;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.FileReader;
import java.io.Reader;

@Component
@Slf4j
public class CommandRunner implements org.springframework.boot.CommandLineRunner {
    CommandLine.IFactory factory;
    ConnectionCommand connectionCommand;
    int exitCode;

    // constructor injection
    CommandRunner(CommandLine.IFactory factory, ConnectionCommand connectionCommand) {
        this.factory = factory;
        this.connectionCommand = connectionCommand;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Connect Test {}", "Start");
        exitCode = new CommandLine(connectionCommand, factory).execute(args);
    }
}
