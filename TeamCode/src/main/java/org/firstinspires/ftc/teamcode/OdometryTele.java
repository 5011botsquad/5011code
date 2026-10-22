package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Field Centric")
public class OdometryTele extends OpMode {

    GoBildaPinpointDriver odo;
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;

    private final double XOffset = 3;
    private final double YOffset = 3;

    @Override
    public void init() {
        FL = hardwareMap.get(DcMotor.class,"frontleft");
        FR = hardwareMap.get(DcMotor.class,"frontright");
        BL = hardwareMap.get(DcMotor.class,"backleft");
        BR = hardwareMap.get(DcMotor.class,"backright");

        FL.setDirection(DcMotorSimple.Direction.FORWARD);
        FR.setDirection(DcMotorSimple.Direction.FORWARD);
        BL.setDirection(DcMotorSimple.Direction.FORWARD);
        BR.setDirection(DcMotorSimple.Direction.FORWARD);
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo"); // match your config name
        odo.setOffsets(XOffset,YOffset, DistanceUnit.INCH); // Offsets then type of unit
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD); //changes based on model (use first option)
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        odo.resetPosAndIMU();
        Pose2D startingPosition = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.RADIANS, 0);
        odo.setPosition(startingPosition);

        //add sum telemetry
    }

    public void loop(){
        odo.update();
        MoveRobot();
    }

    public void MoveRobot(){

        double foward = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        Pose2D pos = odo.getPosition();
        double heading = pos.getHeading(AngleUnit.RADIANS);

        double cosAngle = Math.cos(heading);
        double sinAngle = Math.sin(heading);

        double globalForward = foward * cosAngle - strafe * sinAngle;
        double globalStrafe = foward * sinAngle + strafe * cosAngle;

        double[] newWheelSpeeds = new double[4];

        newWheelSpeeds[0] = globalForward + globalStrafe + rotate;
        newWheelSpeeds[1] = globalForward - globalStrafe - rotate;
        newWheelSpeeds[2] = globalForward - globalStrafe + rotate;
        newWheelSpeeds[3] = globalForward + globalStrafe - rotate;

        FL.setPower(newWheelSpeeds[0]);
        FR.setPower(newWheelSpeeds[1]);
        BL.setPower(newWheelSpeeds[2]);
        BR.setPower(newWheelSpeeds[3]);

        //add sum telemtry

    }
}