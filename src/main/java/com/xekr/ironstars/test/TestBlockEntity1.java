package com.xekr.ironstars.test;

import com.xekr.ironstars.blocks.entity.AbstractEFFBlockEntity;
import com.xekr.ironstars.registry.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TestBlockEntity1 extends AbstractEFFBlockEntity {

    public TestBlockEntity1(BlockPos pos, BlockState state) {
        super(AllBlockEntities.TEST1, pos, state);
    }

    @Override
    public boolean isSourceMachine() {
        return false;
    }

    @Override
    public int getMachineEfficiency() {
        return 0;
    }

    @Override
    public boolean canInput(Direction direction) {
        return true;
    }

    @Override
    public Set<Direction> getOutputSide() {
        return Arrays.stream(Direction.values()).collect(Collectors.toSet());
    }
}
