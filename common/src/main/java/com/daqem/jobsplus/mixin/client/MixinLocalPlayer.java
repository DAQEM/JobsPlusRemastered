package com.daqem.jobsplus.mixin.client;

import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.JobManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer extends AbstractClientPlayer implements JobsClientPlayer {

    private List<Job> jobs = new ArrayList<>();
    private int coins = 0;

    public MixinLocalPlayer(ClientLevel clientLevel, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(clientLevel, gameProfile, profilePublicKey);
    }


    @Override
    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public List<JobInstance> getJobInstances() {
        return jobs.stream().map(Job::getJobInstance).toList();
    }

    @Override
    public List<Job> getInactiveJobs() {
        return JobManager.getInstance().getJobs().values().stream()
                .filter(jobInstance -> !getJobInstances().contains(jobInstance))
                .map(jobInstance -> new Job(this, jobInstance))
                .toList();
    }

    @Nullable
    @Override
    public Job addNewJob(JobInstance jobInstance) {
        if (jobInstance.getLocation() == null) return null;
        Job job = getJob(jobInstance);
        if (job == null) {
            job = new Job(this, jobInstance, 1, 0);
            jobs.add(job);
            return job;
        }
        return null;
    }

    @Override
    public void removeJob(JobInstance job) {
        jobs.remove(getJob(job));
    }

    @Override
    public Job getJob(@Nullable JobInstance jobLocation) {
        return jobs.stream().filter(job -> job.getJobInstance().equals(jobLocation)).findFirst().orElse(null);
    }

    @Override
    public int getCoins() {
        return this.coins;
    }

    @Override
    public void addCoins(int coins) {
        this.coins += coins;
    }

    @Override
    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public String name() {
        return getLocalPlayer().getName().getString();
    }

    @Override
    public double nextRandomDouble() {
        return getLocalPlayer().getRandom().nextDouble();
    }

    @Override
    public @NotNull UUID getUUID() {
        return super.getUUID();
    }

    @Override
    public Level level() {
        return getLocalPlayer().getLevel();
    }

    @Override
    public Player getPlayer() {
        return getLocalPlayer();
    }

    @Override
    public LocalPlayer getLocalPlayer() {
        return (LocalPlayer) (Object) this;
    }
}
