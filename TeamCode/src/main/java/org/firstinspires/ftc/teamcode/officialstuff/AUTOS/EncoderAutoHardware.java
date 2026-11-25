package org.firstinspires.ftc.teamcode.officialstuff.AUTOS;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.FlyWheelAdvanced.FlyWheel;

public class EncoderAutoHardware {

    private final LinearOpMode opMode;

    public DcMotor frontLeft, frontRight, backLeft, backRight;
    public DcMotorEx intakeMotor, transferMotor;

    public FlyWheel shooterMotor = new FlyWheel();

    public static final double SHOOTER_RPM = 3650;
    public static final double INTAKE_RPM = 1100;
    public static final double TRANSFER_RPM = 540;

    public static final double INTAKE_TICKS_PER_REV = 28;
    public static final double TRANSFER_TICKS_PER_REV = 103.8;

    public static final double RPM_TOLERANCE = 100;
    public static final double RPM_DROP_THRESHOLD = 100;

    public static final double SHOOT_DURATION_SECONDS = 6.0;
    public static final double SPINUP_TIMEOUT_SECONDS = 6.0;
    public static final double WHEEL_TICKS_PER_REV = 537.7;
    public static final double WHEEL_DIAMETER_INCHES = 4.0;

    public static final double COUNTS_PER_INCH =
            WHEEL_TICKS_PER_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    public static final double STRAFE_CORRECTION = 1;
    public static final double DRIVE_POWER = 0.5;
    public static final double DRIVE_TIMEOUT = 5.0;

    public EncoderAutoHardware(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void setMotorRPM(DcMotorEx motor, double rpm, double ticksPerRev) {
        double ticksPerSecond = (rpm / 60.0) * ticksPerRev;
        motor.setVelocity(ticksPerSecond);
    }

    public void init() {

        shooterMotor.init(opMode.hardwareMap);

        intakeMotor = opMode.hardwareMap.get(DcMotorEx.class, "intakeMotor");
        transferMotor = opMode.hardwareMap.get(DcMotorEx.class, "transferMotor");

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        transferMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transferMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        transferMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeft = opMode.hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = opMode.hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = opMode.hardwareMap.get(DcMotor.class, "backLeft");
        backRight = opMode.hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void shoot() {

        ElapsedTime spinupTimer = new ElapsedTime();
        shooterMotor.setMotorRPM(SHOOTER_RPM);

        while (opMode.opModeIsActive()
                && Math.abs(SHOOTER_RPM - shooterMotor.getRPM()) > RPM_TOLERANCE
                && spinupTimer.seconds() < SPINUP_TIMEOUT_SECONDS) {

            shooterMotor.setMotorRPM(SHOOTER_RPM);

            opMode.telemetry.addData("Status", "Spinning up");
            opMode.telemetry.addData("Target RPM", SHOOTER_RPM);
            opMode.telemetry.addData("Actual RPM", shooterMotor.getRPM());
            opMode.telemetry.update();
        }

        if (!opMode.opModeIsActive()) {
            stopAll();
            return;
        }

        ElapsedTime shootingTimer = new ElapsedTime();

        boolean feeding = false;

        while (opMode.opModeIsActive()
                && shootingTimer.seconds() < SHOOT_DURATION_SECONDS) {

            double actualRPM = shooterMotor.getRPM();

            shooterMotor.setMotorRPM(SHOOTER_RPM);

            if (!feeding) {
                if (Math.abs(SHOOTER_RPM - actualRPM) < RPM_TOLERANCE) {
                    feeding = true;
                }
            } else {
                if (actualRPM < SHOOTER_RPM - RPM_DROP_THRESHOLD) {
                    feeding = false;
                }
            }

            if (feeding) {
                setMotorRPM(intakeMotor, INTAKE_RPM, INTAKE_TICKS_PER_REV);
                setMotorRPM(transferMotor, TRANSFER_RPM, TRANSFER_TICKS_PER_REV);
            } else {
                setMotorRPM(intakeMotor, 0, INTAKE_TICKS_PER_REV);
                setMotorRPM(transferMotor, 0, TRANSFER_TICKS_PER_REV);
            }

            opMode.telemetry.addData("Status", feeding ? "Feeding" : "Recovering");
            opMode.telemetry.addData("Target RPM", SHOOTER_RPM);
            opMode.telemetry.addData("Actual RPM", actualRPM);
            opMode.telemetry.addData("Shooting Time", shootingTimer.seconds());
            opMode.telemetry.update();
        }

        stopAll();
    }

    public void strafe(double inches) {

        int counts = (int) (inches * STRAFE_CORRECTION * COUNTS_PER_INCH);

        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + counts);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + counts);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + counts);
        backRight.setTargetPosition(backRight.getCurrentPosition() + counts);

        runDriveToPosition();
    }

    public void driveForward(double inches) {

        int counts = (int) (inches * COUNTS_PER_INCH);

        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + counts);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() - counts);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() - counts);
        backRight.setTargetPosition(backRight.getCurrentPosition() + counts);

        runDriveToPosition();
    }

    private void runDriveToPosition() {

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(DRIVE_POWER);
        frontRight.setPower(DRIVE_POWER);
        backLeft.setPower(DRIVE_POWER);
        backRight.setPower(DRIVE_POWER);

        ElapsedTime driveTimer = new ElapsedTime();

        while (opMode.opModeIsActive()
                && driveTimer.seconds() < DRIVE_TIMEOUT
                && (frontLeft.isBusy()
                || frontRight.isBusy()
                || backLeft.isBusy()
                || backRight.isBusy())) {

            opMode.telemetry.addData("Status", "Driving");
            opMode.telemetry.addData("FL", frontLeft.getCurrentPosition());
            opMode.telemetry.addData("FR", frontRight.getCurrentPosition());
            opMode.telemetry.addData("BL", backLeft.getCurrentPosition());
            opMode.telemetry.addData("BR", backRight.getCurrentPosition());
            opMode.telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void stopAll() {

        shooterMotor.stopMotor();

        if (intakeMotor != null) setMotorRPM(intakeMotor, 0, INTAKE_TICKS_PER_REV);
        if (transferMotor != null) setMotorRPM(transferMotor, 0, TRANSFER_TICKS_PER_REV);

        if (frontLeft != null) frontLeft.setPower(0);
        if (frontRight != null) frontRight.setPower(0);
        if (backLeft != null) backLeft.setPower(0);
        if (backRight != null) backRight.setPower(0);
    }
}