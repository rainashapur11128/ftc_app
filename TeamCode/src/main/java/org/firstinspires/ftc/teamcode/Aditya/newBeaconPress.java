package org.firstinspires.ftc.teamcode.Aditya;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.AutonomousGeneral;

/**
 * Created by adityamavalankar on 1/13/17.
 */
@Autonomous(name = "newBeaconPressAuto")
public class newBeaconPress extends AutonomousGeneral {

    String currentTeam = "red";
    String currentColor = "blank";

    @Override
    public void runOpMode() {

        initiate();

        readNewColor();

        waitForStart();


        if (colorSensor.red() > colorSensor.blue()) {
            currentColor = "red";
        } else if (colorSensor.red() < colorSensor.blue()) {
            currentColor = "blue";
        }

        if (currentColor.equals(currentTeam)) {

            encoderDrive(0.25, 17, 17, 7);

            sleep(400);

            encoderDrive(0.5, -15, -15, 6);

        } else {

            encoderDrive(0.5, -7, -7, 5);

            sleep(500);

            encoderDrive(0.4, -7.5, 7.5, 4);
            //currently, this turn value is made for alpha. Adjust for beta

            sleep(150);

            encoderDrive(0.25, 28, 28, 8);

            sleep(400);

            encoderDrive(0.5, -15, -15, 6);

            sleep(150);

            encoderDrive(0.7, 7.5, -7.5, 7);
            //currently, this turn value is made for alpha. Adjust for beta
        }
    }

    public void readNewColor() {

        currentColor = "blank";

        if (colorSensor.red() > colorSensor.blue()) {
            currentColor = "red";

            telemetry.addData("current color is red", colorSensor.red());
            telemetry.update();
        } else if (colorSensor.red() < colorSensor.blue()) {
            currentColor = "blue";

            telemetry.addData("current color is blue", colorSensor.blue());
            telemetry.update();

        } else {

            currentColor = "blank";
        }
    }
}

//-------------------------------------------------------------------//

