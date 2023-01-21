package com.daqem.jobsplus.event.command;

import com.daqem.jobsplus.command.JobCommand;
import dev.architectury.event.events.common.CommandRegistrationEvent;

public class EventRegisterCommands {

    public static void registerEvent() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> JobCommand.registerCommand(dispatcher));
    }
}
