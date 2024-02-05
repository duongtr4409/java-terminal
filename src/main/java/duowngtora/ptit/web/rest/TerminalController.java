package duowngtora.ptit.web.rest;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TerminalController {

    @Value("${myApplication.locationFile:/bin/bash}")
    private String locationFile;

    @PostMapping("ExecuteCommand/Terminal")
    public ResponseEntity<String> execCommand(@RequestBody String command) throws Exception {
        boolean isWindowns = System.getProperty("os.name").toLowerCase().startsWith("windows");
        //        File location = new File("C:\\Users\\DuowngTora\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\System Tools");
        File location = new File(locationFile);
        return ResponseEntity.ok().body(this.runCommand(location, command, isWindowns));
    }

    // utils \\
    public String runCommand(File whereToRun, String command, boolean isWindows) throws Exception {
        System.out.println("Running in: " + whereToRun);
        System.out.println("Command: " + command);
        String commandPrint = "Running in: " + whereToRun + "\r\nCommand: " + command + "\r\n";

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(whereToRun);

        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }

        Process process = builder.start();

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        String successResult = printStream(inputStream);
        String errorResult = printStream(errorStream);

        boolean isFinished = process.waitFor(60, TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if (!isFinished) {
            process.destroyForcibly();
        }
        if (Strings.isNotBlank(errorResult)) return "Error " + commandPrint + errorResult;
        if (command.trim().toLowerCase().startsWith("cd")) {
            if (Strings.isNotBlank(successResult)) {
                this.locationFile = successResult.trim().replaceAll("(\\r|\\n)", "");
            } else {
                String changeLocationFile = command.replaceFirst("cd", "").trim();
                String newLocationFile = Path.of(this.locationFile, changeLocationFile).toString();
                if (new File(newLocationFile).exists()) {
                    this.locationFile = newLocationFile;
                } else if (new File(changeLocationFile).exists()) {
                    this.locationFile = changeLocationFile;
                }
            }
        }
        return commandPrint + successResult;
    }

    private String printStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                result.append("\r\n" + line);
            }
        }
        return result.toString();
    }
}
