package org.firstinspires.ftc.teamcode.officialstuff.AUTOS;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Shoot Then Strafe")
public class ShootThenStrafe extends LinearOpMode {

    private EncoderAutoHardware robot = new EncoderAutoHardware(this);

    @Override
    public void runOpMode() {

        robot.init();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Shoot RPM", EncoderAutoHardware.SHOOTER_RPM);
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            robot.shoot();

            if (opModeIsActive()) {
                robot.driveForward(4);
                robot.strafe(-47);
                robot.driveForward(79);
            }
        }

        robot.stopAll();
    }
}