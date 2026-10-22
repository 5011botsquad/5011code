package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Color")
public class Color extends OpMode {
    Limelight3A limelight;
    ElapsedTime switchTimer = new ElapsedTime();
    ElapsedTime totalTimer = new ElapsedTime();
    int[] Pipelines = {1, 2, 3}; //red blue yellow corresponding
    String[] Colors = {"Red", "Blue", "Yellow"};
    double checktime = 0.3;
    int index = 0;
    String detectedColor = "None";
    double blobX = 0;
    double blobY = 0;
    double blobArea = 0;

    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(Pipelines[index]);
        limelight.start();
        telemetry.addData("Status", "Initialized" +
                "");
        telemetry.update();
    }

    public void start() {
        totalTimer.reset();
        switchTimer.reset();
    }

    public void loop() {
        if (totalTimer.seconds() > 20){
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()
                    && result.getPipelineIndex() == Pipelines[index]) {
                if (!result.getColorResults().isEmpty()) {
                    LLResultTypes.ColorResult blob = result.getColorResults().get(0);
                    detectedColor = Colors[index];
                    blobX = blob.getTargetXDegrees();
                    blobY = blob.getTargetYDegrees();
                    blobArea = blob.getTargetArea();
                } else {
                    detectedColor = "None";
                    blobX = 0;
                    blobY = 0;
                    blobArea = 0;
                }
            }

            if (switchTimer.seconds() > checktime) {
                if (detectedColor.equals("None")) {
                    index = (index + 1)%  Pipelines.length;
                    limelight.pipelineSwitch(Pipelines[index]);
                    switchTimer.reset();
                }
            }

            telemetry.addData("Current Pipeline", Pipelines[index]);
            telemetry.addData("Detected Color", detectedColor);
            telemetry.addData("X (deg)", "%.2f", blobX);
            telemetry.addData("Y (deg)", "%.2f", blobY);
            telemetry.addData("Area", "%.2f%%", blobArea);
            telemetry.update();
        }
    }

    public void stop() {
        limelight.stop();
    }
}