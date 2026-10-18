package com.observatory.subsystem;

public record StackedImage(int frameCount, double totalExposureSeconds, double signalToNoiseGain) { }
