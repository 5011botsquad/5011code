package org.firstinspires.ftc.teamcode.officialstuff.AUTOS;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "ShootSecond")
public class ShootSecond extends LinearOpMode {

    private EncoderAutoHardware robot = new EncoderAutoHardware(this);

    @Override
    public void runOpMode() {

        robot.init();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Shoot RPM", EncoderAutoHardware.SHOOTER_RPM);
        telemetry.update();

        waitForStart();
        sleep(15000); //15 Seconds

        if (opModeIsActive()) {

            robot.shoot();

            if (opModeIsActive()) {
                robot.driveForward(12);
                robot.strafe(50);
            }
        }

        robot.stopAll();
    }
}