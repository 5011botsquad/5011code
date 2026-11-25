package org.firstinspires.ftc.teamcode.officialstuff.AUTOS;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "StrafeSimple")
public class StrafeSimple extends LinearOpMode {

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
                robot.strafe(13);
            }
        }

        robot.stopAll();
    }
}