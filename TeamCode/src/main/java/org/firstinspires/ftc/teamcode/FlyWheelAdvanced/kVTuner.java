package org.firstinspires.ftc.teamcode.FlyWheelAdvanced;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp

public class kVTuner extends OpMode {

    FlyWheel flyWheel = new FlyWheel();

    public static double kV = 0.0001330; //can be linear for now but we will need to make some chart as inconsistent fo wide varying rpms
    public double kS = 0.065320; //our own
    public double goalRPM = 3000;
    double[] increments = {0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndx = 4;

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

        if (gamepad1.dpadUpWasPressed()) {kV += currentStep;}
        if (gamepad1.dpadDownWasPressed()) {kV -= currentStep;}

        double power = (kV*goalRPM) + kS; //feed foward
        flyWheel.setShooterPower(power);

        telemetry.addData("Step", "%.7f", currentStep);
        telemetry.addData("kV", "%.7f", kV);
        telemetry.addData("RPM","%.7f", Math.abs(flyWheel.getRPM()));
        telemetry.addData("Ticks per Sec","%.7f", flyWheel.getTicksPerSec());
    }
}
