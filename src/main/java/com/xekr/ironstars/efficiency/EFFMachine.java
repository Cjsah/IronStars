package com.xekr.ironstars.efficiency;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.Set;

public interface EFFMachine {
    /**
     * 电力网络getter
     */
    @Nullable
    EFFNetwork getNetwork();

    /**
     * 电力网络setter
     */
    void setNetwork(@Nullable EFFNetwork network);

    /**
     * 判断是否在电力网络中
     */
    boolean hasNetwork();

    /**
     * 是否是发电机
     */
    boolean isSourceMachine();

    /**
     * 如果是发电机, 此为发电效率
     * <br>
     * 如果是用电器, 此为用电效率
     */
    int getMachineEfficiency();

    /**
     * 电力可输入方向
     * <br>
     * 输入方向优先于输出方向
     */
    boolean canInput(Direction direction);

    /**
     * 电力可输出方向
     * <br>
     * 输入方向优先于输出方向
     */
    Set<Direction> getOutputSide();
}
