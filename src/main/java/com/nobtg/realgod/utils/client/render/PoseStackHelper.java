package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public final class PoseStackHelper {
    public static void pushPose(PoseStack stack) {
        PoseStack.Pose posestack$pose = stack.poseStack.getLast();
        stack.poseStack.addLast(new PoseStack.Pose(new Matrix4f(posestack$pose.pose), new Matrix3f(posestack$pose.normal)));
    }

    public static void setIdentity(PoseStack stack) {
        PoseStack.Pose posestack$pose = stack.poseStack.getLast();
        Matrix4fHelper.identity(posestack$pose.pose);
        Matrix3fHelper.memUtilIdentity(posestack$pose.normal);
    }

    public static void translate(PoseStack stack, double p_85838_, double p_85839_, double p_85840_) {
        translate(stack, (float) p_85838_, (float) p_85839_, (float) p_85840_);
    }

    public static void translate(PoseStack stack, float p_254202_, float p_253782_, float p_254238_) {
        PoseStack.Pose posestack$pose = stack.poseStack.getLast();
        Matrix4fHelper.translation(posestack$pose.pose, p_254202_, p_253782_, p_254238_);
    }

    public static void popPose(PoseStack stack) {
        stack.poseStack.removeLast();
    }

    public static PoseStack.Pose last(PoseStack stack) {
        return stack.poseStack.getLast();
    }
}
