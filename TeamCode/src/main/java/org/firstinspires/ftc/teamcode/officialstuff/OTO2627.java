
package org.firstinspires.ftc.teamcode.officialstuff;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.FlyWheelAdvanced.FlyWheel;

@TeleOp(name = "OTO2627")
public class OTO2627 extends OpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotorEx transferMotor, intakeMotor;

    private FlyWheel shooterMotor = new FlyWheel();

    private static final double SHOOTER_RPM = 3675; //Max 6000
    private static final double INTAKE_RPM = 700; //Max 6000
    private static final double TRANSFER_RPM = 420; //Max 1620
    private static final double IDLE_RPM = -2100; //Max 6000

    private static final double INTAKE_TICKS_PER_REV = 28;
    private static final double TRANSFER_TICKS_PER_REV = 103.8;

    private boolean windingUp = false;
    private boolean shooting = false;

    private double targetRPM = SHOOTER_RPM;

    private enum ShootState {
        WAITING_FOR_READY,
        FEEDING
    }

    private ShootState shootState = ShootState.WAITING_FOR_READY;

    private static final double RPM_TOLERANCE = 100;
    private static final double RPM_DROP_THRESHOLD = 100;

    private void setMotorRPM(DcMotorEx motor, double rpm, double ticksPerRev) {
        double ticksPerSecond = (rpm / 60.0) * ticksPerRev;
        motor.setVelocity(ticksPerSecond);
    }


    @Override
    public void init() {

        shooterMotor.init(hardwareMap);

        transferMotor = hardwareMap.get(DcMotorEx.class, "transferMotor");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        transferMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        transferMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        transferMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

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


    @Override
    public void loop() {

        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        double frontLeftPower = axial - lateral - yaw;
        double frontRightPower = -axial - lateral - yaw;
        double backLeftPower = -axial - lateral + yaw;
        double backRightPower = axial - lateral + yaw;

        double max = Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
        );

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        boolean shootPressed = gamepad1.leftBumperWasPressed();
        boolean windPressed = gamepad1.rightBumperWasPressed();

        if (gamepad1.dpadLeftWasPressed()) {
            targetRPM += 50;
        }

        if (gamepad1.dpadRightWasPressed()) {
            targetRPM -= 50;
        }

        targetRPM = Math.max(0, Math.min(6000, targetRPM));

        if (windPressed && !shooting) {
            windingUp = !windingUp;
        }

        if (shootPressed && !windingUp) {
            shooting = !shooting;
            shootState = ShootState.WAITING_FOR_READY;
        }

        double actualRPM = shooterMotor.getRPM();

        if (windingUp || shooting) {
            shooterMotor.setMotorRPM(targetRPM);
        }

        double intakeCommandRPM = 0;
        double transferCommandRPM = 0;

        if (shooting) {
            switch (shootState) {
                case WAITING_FOR_READY:
                    if (Math.abs(targetRPM - actualRPM) < RPM_TOLERANCE) {
                        shootState = ShootState.FEEDING;
                    }
                    break;
                case FEEDING:
                    intakeCommandRPM = INTAKE_RPM;
                    transferCommandRPM = TRANSFER_RPM;
                    if (actualRPM < targetRPM - RPM_DROP_THRESHOLD) {
                        shootState = ShootState.WAITING_FOR_READY;
                    }

                    break;
            }
        } else {
            shootState = ShootState.WAITING_FOR_READY;
            if (gamepad1.right_trigger_pressed) {
                intakeCommandRPM = INTAKE_RPM;
                transferCommandRPM = TRANSFER_RPM;
            } else if (gamepad1.left_trigger_pressed) {
                intakeCommandRPM = -INTAKE_RPM;
                transferCommandRPM = -TRANSFER_RPM;
            } else if (gamepad1.x) {
                intakeCommandRPM = INTAKE_RPM;
                transferCommandRPM = TRANSFER_RPM;
            } else if (gamepad1.y) {
                intakeCommandRPM = -INTAKE_RPM;
                transferCommandRPM = -TRANSFER_RPM;
            } else if (gamepad1.a) {
                intakeCommandRPM = INTAKE_RPM;
            } else if (gamepad1.b) {
                intakeCommandRPM = -INTAKE_RPM;
            } else if (gamepad1.dpad_up) {
                transferCommandRPM = TRANSFER_RPM;
            } else if (gamepad1.dpad_down) {
                transferCommandRPM = -TRANSFER_RPM;
            }
        }

        setMotorRPM(intakeMotor, intakeCommandRPM, INTAKE_TICKS_PER_REV);
        setMotorRPM(transferMotor, transferCommandRPM, TRANSFER_TICKS_PER_REV);

        if (!windingUp && !shooting) {
            if (intakeCommandRPM != 0 || transferCommandRPM != 0) {
                shooterMotor.setMotorRPM(IDLE_RPM);
            } else {
                shooterMotor.stopMotor();
            }
        }

        double intakeActualRPM =
                intakeMotor.getVelocity() / INTAKE_TICKS_PER_REV * 60.0;
        double transferActualRPM =
                transferMotor.getVelocity() / TRANSFER_TICKS_PER_REV * 60.0;

        telemetry.addData("Target RPM", "%.1f", targetRPM);
        telemetry.addData("Raw RPM", "%.1f", shooterMotor.getRawRPM());
        telemetry.addData("Actual RPM", "%.1f", actualRPM);
        telemetry.addData("RPM Error", "%.1f", targetRPM - actualRPM);
        telemetry.addData("Shooter Power", "%.3f", shooterMotor.getMotorPower());

        telemetry.addData("Winding Up", windingUp);
        telemetry.addData("Shooting", shooting);
        telemetry.addData("Shoot State", shootState);

        telemetry.addData("Intake Target RPM", "%.1f", intakeCommandRPM);
        telemetry.addData("Intake Actual RPM", "%.1f", intakeActualRPM);

        telemetry.addData("Transfer Target RPM", "%.1f", transferCommandRPM);
        telemetry.addData("Transfer Actual RPM", "%.1f", transferActualRPM);

        telemetry.update();
    }
}
