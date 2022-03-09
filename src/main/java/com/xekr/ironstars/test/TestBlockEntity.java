package com.xekr.ironstars.test;

import com.xekr.ironstars.blocks.entity.AbstractEFFBlockEntity;
import com.xekr.ironstars.registry.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TestBlockEntity extends AbstractEFFBlockEntity {

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.TEST, pos, state);
    }

    @Override
    public boolean isSourceMachine() {
        return true;
    }

    @Override
    public int getMachineEfficiency() {
        return 128;
    }

    @Override
    public boolean canInput(Direction direction) {
        return false;
    }

    @Override
    public Set<Direction> getOutputSide() {
        return Arrays.stream(Direction.values()).collect(Collectors.toSet());
    }
}
