package org.firstinspires.ftc.teamcode.Salsa.OpModes;

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Salsa.Constants;
import org.firstinspires.ftc.teamcode.Salsa.Hardware.Robot;
import org.firstinspires.ftc.teamcode.Salsa.Vision.SamplingDetector;
import org.opencv.core.Mat;

import com.qualcomm.robotcore.util.Range;


/**
 * Created by adityamavalankar on 11/19/18.
 */

public abstract class SalsaLinearOpMode extends LinearOpMode {

    /**
     * This is a modified version of the {LinearOpMode} class, with all of the functions
     * meant for autonomous, without having extra work to make them, and the autonomous LinearOpMode
     * to both work
     */

    public Robot robot = new Robot();
    public Constants constants;
    private ElapsedTime runtime = new ElapsedTime();

    /**
     *
     * @param leftTarget
     * @param rightTarget
     */

    public void setTargetPosition(int leftTarget, int rightTarget) {
        robot.leftFront.setTargetPosition(robot.leftFront.getCurrentPosition() + leftTarget);
        robot.rightFront.setTargetPosition(robot.rightFront.getCurrentPosition() + rightTarget);
        robot.leftBack.setTargetPosition(robot.leftBack.getCurrentPosition() + leftTarget);
        robot.rightBack.setTargetPosition(robot.rightBack.getCurrentPosition() + rightTarget);
    }

    public void setPower(double power) {
        robot.leftFront.setPower(power);
        robot.leftBack.setPower(power);
        robot.rightFront.setPower(power);
        robot.rightBack.setPower(power);

    }

    /**
     * We first convert the inputted distance (in cm) to encoder ticks with the constants.TICKS_PER_CM constant, while
     * setting motors to the runMode RUN_TO_POSITION.
     * Then, we see the expected time the motors should run at using the { timeSec() } function
     * Then, we set the target position, set motors to move at desired speed, until we hit the correct position
     * Then, we set speed to zero
     * @param left_cm
     * @param speed
     */

    public void encoderDriveCM(double left_cm, double right_cm, double speed, double timeoutS) {


        int left_distanceEnc = (int)(constants.TICKS_PER_CM * left_cm);
        int right_distanceEnc = (int)(constants.TICKS_PER_CM * right_cm);

        setTargetPosition(left_distanceEnc, right_distanceEnc);
        setMotorRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        setPower(Math.abs(speed));
        runtime.reset();

        while(this.opModeIsActive() && robot.leftFront.isBusy() && (runtime.seconds() < timeoutS) && robot.leftBack.isBusy() && robot.rightFront.isBusy() && robot.rightBack.isBusy()) {
            telemetry.addLine("Robot in Encoder Drive");
            telemetry.addData("Target Distance Left (cm)", left_cm);
            telemetry.addData("Target Distance Right (cm)", right_cm);
            telemetry.update();
        }

        setPower(0);

        setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void encoderDriveIN(double left_in, double right_in, double speed, double timeoutS) {

        int left_distanceEnc = (int)(constants.TICKS_PER_IN * left_in);
        int right_distanceEnc = (int)(constants.TICKS_PER_IN * right_in);

        setTargetPosition(left_distanceEnc, right_distanceEnc);
        setMotorRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        setPower(Math.abs(speed));
        runtime.reset();

        while(this.opModeIsActive() && (runtime.seconds() < timeoutS) && robot.leftFront.isBusy() && robot.leftBack.isBusy() && robot.rightFront.isBusy() && robot.rightBack.isBusy()) {
            telemetry.addLine("Robot in Encoder Drive");
            telemetry.addData("Target Distance Left (in)", left_in);
            telemetry.addData("Target Distance Right (in)", right_in);
            telemetry.update();
            //just one more test...
        }

        setPower(0);

        setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void setMotorRunMode(DcMotor.RunMode runmode) {
        robot.leftFront.setMode(runmode);
        robot.rightFront.setMode(runmode);
        robot.leftBack.setMode(runmode);
        robot.rightBack.setMode(runmode);
    }

    /**
     * The expected drive time is calculated with some math. We multiply speed by RPM of motor, and multiply that
     * by wheel circumference. Then, we divide the distance to be traveled by the total speed per minute.
     * Then, we will convert from minutes to seconds
     * @param cm
     * @param speed
     * @return The total distance that movement should take, multiplied by 1.5 for padding
     */

    public double calculateDriveTimeCM(double cm, double speed) {

        if(cm == 0) {
            return 0;
        }

        else {
            double abs_speed_unc = Math.abs(constants.NEVEREST_40_RPM);
            double abs_distCM = Math.abs(cm);

            double circ = constants.WHEEL_CIRCUMFERENCE_CM;
            double dist_perMin = (abs_speed_unc * circ);
            double timeMin = (dist_perMin / abs_distCM);
            double timeSec = (timeMin * 60);

            return Math.abs(timeSec * constants.ENC_DRIVE_TIME_MULTIPLIER);
        }
    }

    public double calculateDriveTimeIN(double in, double speed) {

        if(in == 0) {
            return 0;
        }

        else {
            double abs_speed_unc = Math.abs(constants.NEVEREST_40_RPM);
            double abs_distIN = Math.abs(in);

            double circ = constants.WHEEL_CIRCUMFERENCE_IN;
            double dist_perMin = (abs_speed_unc * circ);
            double timeMin = (dist_perMin / abs_distIN);
            double timeSec = (timeMin * 60);

            return Math.abs(timeSec * constants.ENC_DRIVE_TIME_MULTIPLIER);
        }
    }

    public void gyroTurn(double speed, double angle, double seconds) {

        runtime.reset();
        telemetry.addLine("Beginning Gyro Turn");
        telemetry.update();

        while(opModeIsActive() && !onTargetAngle(speed, angle, constants.P_TURN_COEFF) && (runtime.seconds() < seconds)) {
            telemetry.update();
            idle();
        }

        setPower(0);
        telemetry.addLine("Gyro Turn complete");
        telemetry.update();

    }

    public boolean onTargetAngle(double speed, double angle, double PCoeff) {
        double error;
        double steer;
        boolean onTarget = false;
        double leftSpeed;
        double rightSpeed;

        error = getGyroError(angle);

        if (Math.abs(error) <= constants.TURN_THRESHOLD) {

            steer = 0.0;
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        }

        else {

            steer = getGyroSteer(error, PCoeff);
            rightSpeed = speed * steer;
            leftSpeed = -rightSpeed;
        }

        setMotorRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double weightConstant = 1;

        robot.leftFront.setPower(weightConstant*leftSpeed);
        robot.leftBack.setPower(weightConstant*leftSpeed);
        robot.rightFront.setPower(weightConstant*rightSpeed);
        robot.rightBack.setPower(weightConstant*rightSpeed);

        telemetry.addData("Target angle","%5.2f",angle);
        telemetry.addData("Error/Steer", "%5.2f/%5.2f", error, steer);
        telemetry.addData("speed", "%5.2f/%5.2f", leftSpeed, rightSpeed);

        return onTarget;
    }

    public double getGyroError(double targetAngle) {

        double robotError;
        robotError = targetAngle - robot.angles.firstAngle;

        while(robotError > 180) robotError -= 360;

        while(robotError <= -180) robotError += 360;

        telemetry.addData("Robot Error","%5.2f",robotError);
        telemetry.update();

        return robotError;
    }

    public double getGyroSteer(double error , double PCoeff){
        if((error*PCoeff) > 0) {
            return Range.clip(error * PCoeff, 0.2, 1);
        }
        else if(error*PCoeff< 0){
            return Range.clip(error * PCoeff, -1, -0.2);
        }
        else{
            return 0;
        }
    }

    public void doSampling() {
        SamplingOrderDetector.GoldLocation samplingOrder = robot.getSamplingOrderSmart();

        sleep(500);

        if(samplingOrder == SamplingOrderDetector.GoldLocation.UNKNOWN) {
            samplingOrder = robot.getSamplingOrderSmart();
        }

        if(samplingOrder == SamplingOrderDetector.GoldLocation.LEFT) {
            encoderDriveIN(-4.5, 4.5, 0.6, 2);
            sleep(150);
            encoderDriveIN(30, 30, 0.6, 3);
            sleep(150);
        }
        else if (samplingOrder == SamplingOrderDetector.GoldLocation.CENTER || samplingOrder == SamplingOrderDetector.GoldLocation.UNKNOWN) {
            encoderDriveIN(30, 30, 0.6, 3);
            sleep(150);
        }
        else if (samplingOrder == SamplingOrderDetector.GoldLocation.RIGHT) {
            encoderDriveIN(4.5, -4.5, 0.6, 2);
            sleep(150);
            encoderDriveIN(30, 30, 0.6, 3);
            sleep(150);
        }

        sleep(500);
    }

    public void doSamplingGyro() {
        SamplingOrderDetector.GoldLocation samplingOrder = robot.getSamplingOrderSmart();

        sleep(500);

        if(samplingOrder == SamplingOrderDetector.GoldLocation.UNKNOWN) {
            samplingOrder = robot.getSamplingOrderSmart();
        }

        if(samplingOrder == SamplingOrderDetector.GoldLocation.LEFT) {
            gyroTurn(0.6, 60, 2);
            sleep(150);
            encoderDriveIN(30, 30, 0.6, 3);
            sleep(150);
        }
        else if (samplingOrder == SamplingOrderDetector.GoldLocation.CENTER) {
            encoderDriveIN(30, 30, 0.6, 3);
            sleep(150);
        }
        else if (samplingOrder == SamplingOrderDetector.GoldLocation.RIGHT) {
            gyroTurn(0.6, -60, 2);
            sleep(150);
            encoderDriveIN(30, 30, 0.6, 3);
            sleep(150);
        }

        sleep(500);
    }

    public void deHang() {
        robot.liftSlides.setPower(1 * constants.LIFT_MOTOR_LOWER_CONSTANT);

        while(!onGround()) {
            idle();
        }
        sleep(250);
        robot.liftSlides.setPower(0);


    }

    public boolean onGround() {
        boolean onGround = false;

        if (robot.groundDistance.getDistance(DistanceUnit.CM) > constants.ON_GROUND_DISTANCE_CM) {
            onGround = false;
        }
        else {
            onGround = true;
        }
        return onGround;
    }

}
