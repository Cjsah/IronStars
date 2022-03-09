package com.xekr.ironstars.efficiency;

import com.xekr.ironstars.IronStarsUtil;
import com.xekr.ironstars.state.NetworkState;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EFFNetwork {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final NetworkState STATE = new NetworkState();
    public static final Map<UUID, EFFNetwork> NETWORK = new LinkedHashMap<>();
    private static boolean frozenRegisterByUUID = false;

    private final UUID id;
    private int total;
    private Level level;
    private final Map<EFFMachine, Integer> sources = new LinkedHashMap<>();
    private final List<EFFMachine> members = new LinkedList<>();

    private EFFNetwork() {
        this(Mth.createInsecureUUID(IronStarsUtil.RANDOM));
    }

    private EFFNetwork(UUID uuid) {
        this.id = uuid;
        this.total = 0;
        this.level = Level.EMPTY;
        NETWORK.put(this.id, this);
    }

    public static EFFNetwork create(EFFMachine machine) {
        LOGGER.info("test");
        if (machine.hasNetwork() || !machine.isSourceMachine()) return null;
        EFFNetwork network = new EFFNetwork();
        network.appendMachine(machine);
        STATE.markDirty();
        return network;
    }

    public static EFFNetwork create(UUID uuid) {
        if (frozenRegisterByUUID) throw new IllegalStateException("Creating network by uuid is already frozen");
        return new EFFNetwork(uuid);
    }

    public static void freeze() {
        frozenRegisterByUUID = true;
    }

    public static ListTag serialize() {
        ListTag list = new ListTag();
//        NETWORK.entrySet().stream()
//                .filter(entry -> !entry.getValue().sources.isEmpty())
//                .forEach(entry -> list.add(StringTag.valueOf(entry.getKey().toString())));
        NETWORK.forEach((key, value) -> list.add(StringTag.valueOf(key.toString())));
        return list;
    }

    public static void merge(EFFNetwork networkA, EFFNetwork networkB) {
        networkB.sources.forEach((key, value) -> key.setNetwork(networkA));
        networkA.sources.putAll(networkB.sources);
        networkB.sources.clear();
        networkB.members.forEach(value -> value.setNetwork(networkA));
        networkA.members.addAll(networkB.members);
        networkB.members.clear();
        networkA.updateOutputLevel();
        networkB.recycle();
    }

    public void updateOutputLevel() {
        this.level = this.members.size() == 0 ? Level.EMPTY : getOutput(this.total / this.members.size());
    }

    public void appendMachine(EFFMachine machine) {
        if (this.sources.containsKey(machine) || this.members.contains(machine)) return;
        machine.setNetwork(this);
        if (machine.isSourceMachine()) {
            int efficiency = machine.getMachineEfficiency();
            this.sources.put(machine, efficiency);
            this.total += efficiency;
        }else this.members.add(machine);
        this.updateOutputLevel();
        STATE.markDirty();
    }

    @Nullable
    public void removeMachine(EFFMachine machine) {
        machine.setNetwork(null);
        if (this.sources.containsKey(machine)) {
            this.total -= this.sources.get(machine);
            this.sources.remove(machine);
            if (sources.isEmpty()) this.recycle();
            this.updateOutputLevel();
        }else if (this.members.contains(machine)) {
            this.members.remove(machine);
            this.updateOutputLevel();
        }
        STATE.markDirty();
    }

    public void recycle() {
        this.sources.forEach((key, value) -> key.setNetwork(null));
        this.members.forEach(value -> value.setNetwork(null));
        NETWORK.remove(this.id);
        STATE.markDirty();
    }

    public UUID getId() {
        return this.id;
    }

    public int getTotal() {
        return this.total;
    }

    public Level getOutputLevel() {
        return this.level;
    }

    public static EFFNetwork getNetwork(UUID uuid) {
        return NETWORK.getOrDefault(uuid, null);
    }

    public static Level getOutput(int input) {
        Level[] values = Level.values();
        for (int i = 1; i < values.length; i++) {
            if (input < values[i].value && input >= values[i-1].value) return values[i-1];
        }
        return Level.MAX;
    }

    public enum Level {
        EMPTY(0),
        LEVEL1(1),
        LEVEL2(2),
        LEVEL3(4),
        LEVEL4(8),
        LEVEL5(16),
        LEVEL6(32),
        LEVEL7(64),
        LEVEL8(128),
        MAX(256);

        private final int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s,total=%s,level=%s,sources=%s,members=%s]", getClass().getName(), this.id, this.total, this.level, this.sources.size(), this.members.size());
    }
}
