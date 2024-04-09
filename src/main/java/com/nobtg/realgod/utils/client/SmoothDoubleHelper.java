package com.nobtg.realgod.utils.client;

import net.minecraft.util.SmoothDouble;

public final class SmoothDoubleHelper {
    public static double getNewDeltaValue(SmoothDouble smoothDouble, double p_14238_, double p_14239_) {
        smoothDouble.targetValue += p_14238_;
        double $$2 = smoothDouble.targetValue - smoothDouble.remainingValue;
        double $$3 = smoothDouble.lastAmount + 0.5 * ($$2 - smoothDouble.lastAmount);
        double $$4 = Math.signum($$2);
        if ($$4 * $$2 > $$4 * smoothDouble.lastAmount) {
            $$2 = $$3;
        }
        smoothDouble.lastAmount = $$3;
        smoothDouble.remainingValue += $$2 * p_14239_;
        return $$2 * p_14239_;
    }

    public static void reset(SmoothDouble smoothDouble) {
        smoothDouble.targetValue = 0.0;
        smoothDouble.remainingValue = 0.0;
        smoothDouble.lastAmount = 0.0;
    }
}
