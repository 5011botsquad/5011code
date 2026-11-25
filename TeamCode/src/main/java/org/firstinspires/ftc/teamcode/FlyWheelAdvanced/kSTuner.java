package org.firstinspires.ftc.teamcode.FlyWheelAdvanced;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp

public class kSTuner extends OpMode {

    FlyWheel flyWheel = new FlyWheel();

    public static double kS = 0;

    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndx = 4;

    public void init(){
        flyWheel.init(hardwareMap);
    }

    public void loop() {
        if (gamepad1.dpadRightWasPressed() && incrementIndx < 4){
            incrementIndx++;
        } else if (gamepad1.dpadLeftWasPressed() && incrementIndx >0){
            incrementIndx--;
        }

        double currentStep = increments[incrementIndx];

        if (gamepad1.dpadUpWasPressed()) {kS += currentStep;}
        if (gamepad1.dpadDownWasPressed()) {kS -= currentStep;}

        flyWheel.setShooterPower(kS);

        telemetry.addData("Step", "%.6f", currentStep);
        telemetry.addData("kS", "%.6f", kS);
        telemetry.addData("RPM","%.6f", flyWheel.getRPM());
        telemetry.addData("Ticks per Sec", "%.6f", flyWheel.getTicksPerSec());

        telemetry.update();
    }
}
