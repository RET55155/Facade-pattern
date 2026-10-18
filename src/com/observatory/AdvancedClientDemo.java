package com.observatory;

import com.observatory.subsystem.CameraController;
import com.observatory.subsystem.Frame;
import com.observatory.subsystem.ImageStacker;

import java.util.List;

public class AdvancedClientDemo {
    public static void main(String[] args) throws Exception {
        CameraController camera = new CameraController();
        camera.coolTo(-25);
        Frame frame = camera.expose(300);
        System.out.println("Direct subsystem use: " + frame);
        System.out.println(new ImageStacker().stack(List.of(frame)));
    }
}
