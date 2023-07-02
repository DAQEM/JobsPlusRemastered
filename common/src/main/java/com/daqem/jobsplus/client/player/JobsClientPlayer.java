package com.daqem.jobsplus.client.player;

import com.daqem.jobsplus.player.JobsPlayer;
import net.minecraft.client.player.LocalPlayer;

public interface JobsClientPlayer extends JobsPlayer {

    LocalPlayer getLocalPlayer();
}
