package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class OuttakeTest extends OpMode {
    DcMotor OuttakeMotor = null;
    boolean lastDpadUp = false;

    public void init(){

        OuttakeMotor = hardwareMap.get(DcMotor.class, "IntakeMotor");
        OuttakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        OuttakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void loop() {
        if (gamepad1.a) {
            OuttakeMotor.setPower(1.0);
        } else {
            OuttakeMotor.setPower(0.0);
        }

        if (gamepad1.dpad_up && !lastDpadUp) {
            if (OuttakeMotor.getDirection() == DcMotorSimple.Direction.FORWARD) {
                OuttakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            } else {
                OuttakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            }
        }
        lastDpadUp = gamepad1.dpad_up;

        telemetry.addData("Intake Dir", OuttakeMotor.getDirection());
        telemetry.update();
    }



}

