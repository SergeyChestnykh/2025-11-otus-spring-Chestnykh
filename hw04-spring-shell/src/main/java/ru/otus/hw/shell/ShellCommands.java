package ru.otus.hw.shell;

import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@AllArgsConstructor
public class ShellCommands {
    private final TestRunnerService testRunnerService;

    @ShellMethod(key = "start", value = "Start testing")
    public void start() {
        testRunnerService.run();
    }
}
