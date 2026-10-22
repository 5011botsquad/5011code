package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "IntakeTest")
public class IntakeTest extends OpMode {

    CRServo IntakeServo = null;

    public void init(){
       IntakeServo = hardwareMap.get(CRServo.class, "IntakeServo");
    }


    public void loop() {
        if (gamepad1.a) {
            IntakeServo.setPower(1.0);
        } else if (gamepad1.b)
        {
            IntakeServo.setPower(-1.0);
        }
        else {
            IntakeServo.setPower(0);
        }
    }



}
