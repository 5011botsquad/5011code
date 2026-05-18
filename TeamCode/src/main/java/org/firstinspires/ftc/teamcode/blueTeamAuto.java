package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="blueTeamAuto")
public class blueTeamAuto extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight = null;

    private Servo claw;
    private double guide;
    private double clawgrab;
    private double leader;
    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_MOTOR_REV    = 1440;
    static final double DRIVE_GEAR_REDUCTION    = 1.0;
    static final double WHEEL_DIAMETER_INCHES   = 4.0;
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED             = 0.6;
    static final double TURN_SPEED              = 0.5;
    static final double STRAFE_SPEED            = 0.5;

    @Override
    public void runOpMode() {

        frontLeft  = hardwareMap.get(DcMotor.class, "front_left");
        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        backLeft   = hardwareMap.get(DcMotor.class, "back_left");
        backRight  = hardwareMap.get(DcMotor.class, "back_right");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Starting at", "%7d :%7d :%7d :%7d",
                frontLeft.getCurrentPosition(), frontRight.getCurrentPosition(),
                backLeft.getCurrentPosition(), backRight.getCurrentPosition());
        telemetry.update();

        waitForStart();

        encoderStrafe(STRAFE_SPEED, 120, 10.0); // Negative values are left
        encoderDrive(DRIVE_SPEED, 30, 30, 6.0);
        clawPosition(guide);

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);
    }
    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {
        int newFLTarget, newFRTarget, newBLTarget, newBRTarget;

        if (opModeIsActive()) {
            newFLTarget = frontLeft.getCurrentPosition()  + (int)(leftInches  * COUNTS_PER_INCH);
            newFRTarget = frontRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newBLTarget = backLeft.getCurrentPosition()   + (int)(leftInches  * COUNTS_PER_INCH);
            newBRTarget = backRight.getCurrentPosition()  + (int)(rightInches * COUNTS_PER_INCH);

            frontLeft.setTargetPosition(newFLTarget);
            frontRight.setTargetPosition(newFRTarget);
            backLeft.setTargetPosition(newBLTarget);
            backRight.setTargetPosition(newBRTarget);

            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            frontLeft.setPower(Math.abs(speed));
            frontRight.setPower(Math.abs(speed));
            backLeft.setPower(Math.abs(speed));
            backRight.setPower(Math.abs(speed));

            while (opModeIsActive() && (runtime.seconds() < timeoutS) &&
                    (frontLeft.isBusy() && frontRight.isBusy() &&
                            backLeft.isBusy()  && backRight.isBusy())) {
                telemetry.addData("Running to",  " %7d :%7d :%7d :%7d",
                        newFLTarget, newFRTarget, newBLTarget, newBRTarget);
                telemetry.update();
            }

            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);

            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(200);
        }
    }
    public void encoderStrafe(double speed, double inches, double timeoutS) {
        int counts = (int)(inches * COUNTS_PER_INCH);

        if (opModeIsActive()) {

            frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + counts);
            frontRight.setTargetPosition(frontRight.getCurrentPosition() - counts);
            backLeft.setTargetPosition(backLeft.getCurrentPosition() - counts);
            backRight.setTargetPosition(backRight.getCurrentPosition() + counts);

            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            frontLeft.setPower(Math.abs(speed));
            frontRight.setPower(Math.abs(speed));
            backLeft.setPower(Math.abs(speed));
            backRight.setPower(Math.abs(speed));

            while (opModeIsActive() && (runtime.seconds() < timeoutS) &&
                    (frontLeft.isBusy() && frontRight.isBusy() &&
                            backLeft.isBusy()  && backRight.isBusy())) {
                telemetry.addData("Strafing to", "%7d :%7d :%7d :%7d",
                        frontLeft.getTargetPosition(), frontRight.getTargetPosition(),
                        backLeft.getTargetPosition(),  backRight.getTargetPosition());
                telemetry.update();
            }

            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);

            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);
        }
    }
    public void clawPosition(double positionV){
        claw.setPosition(positionV);
    }
}