package org.firstinspires.ftc.teamcode.Main.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Main.OpModes.ExtendedOpMode;

@TeleOp(name = "Avocado Secondary TeleOp", group = "Avocado")
public class AvocadoSecondaryTeleOp extends ExtendedOpMode {


    @Override
    public void init() {

        // Initiate all the hardwareMap names
        robot.setHardwareMap(hardwareMap);
        robot.initDrivetrain();
        robot.initTiltingMechanism();
        robot.initHanger();


    }

    @Override
    public void loop() {

        // Run TeleOP methods
        drive(gamepad1.left_stick_y, gamepad1.right_stick_y);
        collect(gamepad1.right_bumper, gamepad1.left_bumper);
        extend(gamepad2.left_stick_y);
        tilt(gamepad2.right_stick_y);
        strafe(gamepad1.dpad_left, gamepad1.dpad_right, gamepad1.dpad_up, gamepad1.dpad_down, 0.5);
        lockServos(gamepad1.x, gamepad1.y);


    }
}
