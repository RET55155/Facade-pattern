package com.observatory.subsystem;

import java.util.List;

public class ImageStacker {
    public StackedImage stack(List<Frame> frames) throws StackingException {
        if (frames == null || frames.isEmpty()) {
            throw new StackingException("Nothing to stack: no frames were captured");
        }
        double totalExposure = frames.stream().mapToDouble(Frame::exposureSeconds).sum();
        return new StackedImage(frames.size(), totalExposure, Math.sqrt(frames.size()));
    }
}
