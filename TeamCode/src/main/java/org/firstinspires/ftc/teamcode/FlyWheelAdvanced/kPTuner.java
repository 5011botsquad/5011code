package org.firstinspires.ftc.teamcode.FlyWheelAdvanced;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp

public class kPTuner extends OpMode {

    FlyWheel flyWheel = new FlyWheel();

    public static double kV = 0.0001330;
    public double kS = 0.065320;
    public double kP = 0.005627;
    public double goalRPM = 3000;
    double[] increments = {0.000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndx = 2;

    public void init(){
        flyWheel.init(hardwareMap);
    }

    public void loop() {
        if (gamepad1.dpadRightWasPressed() && incrementIndx < 5){
            incrementIndx++;
        } else if (gamepad1.dpadLeftWasPressed() && incrementIndx >0){
            incrementIndx--;
        }
        if (gamepad1.a){
            goalRPM = 3000;
        }
        else if (gamepad1.b){
            goalRPM = 4000;
        }

        double currentStep = increments[incrementIndx];

        if (gamepad1.dpadUpWasPressed()) {kP += currentStep;}
        if (gamepad1.dpadDownWasPressed()) {kP -= currentStep;}

        double feedFoward = (kV*goalRPM) + kS; //feed foward
        double error = goalRPM - Math.abs(flyWheel.getRPM());
        double feedBack = error*kP;

        flyWheel.setShooterPower(feedFoward+feedBack);

        telemetry.addData("Step", "%.6f", currentStep);
        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("Error","%.6f", error);
        telemetry.addData("RPM","%.6f", Math.abs(flyWheel.getRPM()));
        telemetry.addData("Ticks per Sec", "%.6f", flyWheel.getTicksPerSec());
    }
}
