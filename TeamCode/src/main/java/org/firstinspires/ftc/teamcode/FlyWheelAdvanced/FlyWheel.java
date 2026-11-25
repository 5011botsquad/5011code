
package org.firstinspires.ftc.teamcode.FlyWheelAdvanced;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    private DcMotorEx shooterMotor;

    public double encoderCPM = 28;
    public double gearRatio = 1;

    public double kV = 0.0001593;
    public double kS = 0.065320;
    public double kP = 0.0001330;

    public void init(HardwareMap hardwareMap) {
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");

        shooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void stopMotor() {
        shooterMotor.setPower(0);
    }

    public void setMotorRPM(double targetRPM) {

        if (targetRPM < 0) {
            double power = -((kV * Math.abs(targetRPM)) + kS);
            power = Math.max(-1, Math.min(0, power));
            setShooterPower(power);
            return;
        }

        double error = targetRPM - getRPM();

        double ff = (kV * targetRPM) + kS;
        double fb = error * kP;

        double power = ff + fb;
        power = Math.max(0, Math.min(1, power));

        setShooterPower(power);
    }

    public void setShooterPower(double power) {
        shooterMotor.setPower(power);
    }
    public double getTicksPerSec() {
        return shooterMotor.getVelocity();
    }
    public double getRawRPM() {
        return (getTicksPerSec() / encoderCPM) * 60.0 / gearRatio;
    }
    public double getRPM() {
        return Math.abs(getRawRPM());
    }
    public double getMotorPower() {
        return shooterMotor.getPower();
    }
}
