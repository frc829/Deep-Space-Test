package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.sun.jdi.VoidValue;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Solenoid;
import frc.Framework.ISystem;
import frc.util.LogitechAxis;
import frc.util.LogitechButton;
import frc.util.LogitechF310;

public class Drive implements ISystem {
	// region Objects
	private Solenoid transmission_1, transmission_2;
	private TalonSRX frontLeft, midLeft, backLeft;
	private TalonSRX frontRight, midRight, backRight;
	// endregion
	// region Variables
	long rightPositionTime;
	private boolean transmissionStatus = false;
	boolean driveInverse = false;
	boolean isControlled = false;
	NeutralMode currentNeutral = NeutralMode.Coast;
	ControlMode currentControl = ControlMode.PercentOutput;
	long lastGearShift;
	long lastPolarShift;
	// region PID
	public static final int ENC_T = 10;
	public static final int PID_SLOT = 0;
	public static final int PID_TIMEOUT = 10;
	public static final long TRANS_DELAY = 500;
	// endregion
	boolean newDistance = true;
	public static final double COUNT_INCH_LOW = 11.892;
	public static final double COUNT_INCH_HIGH = 12.092;
	long lastCamera;
	boolean ledMode = false;

	// endregion
	public Drive() {
		lastCamera = System.currentTimeMillis();
		try {
			frontLeft = new TalonSRX(SystemMap.Drive.FRONT_LEFT);
			midLeft = new TalonSRX(SystemMap.Drive.MID_LEFT);
			backLeft = new TalonSRX(SystemMap.Drive.BACK_LEFT);
			frontRight = new TalonSRX(SystemMap.Drive.FRONT_RIGHT);
			midRight = new TalonSRX(SystemMap.Drive.MID_RIGHT);
			backRight = new TalonSRX(SystemMap.Drive.BACK_RIGHT);

			frontLeft.setNeutralMode(currentNeutral);
			midLeft.setNeutralMode(currentNeutral);
			backLeft.setNeutralMode(currentNeutral);
			frontRight.setNeutralMode(currentNeutral);
			midRight.setNeutralMode(currentNeutral);
			backRight.setNeutralMode(currentNeutral);

			transmission_1 = new Solenoid(SystemMap.Drive.PCM, SystemMap.Drive.TRANS_CHANNEL_OFF);
			transmission_1.set(transmissionStatus);
			transmission_2 = new Solenoid(SystemMap.Drive.PCM, SystemMap.Drive.TRANS_CHANNEL_ON);
			transmission_2.set(!transmissionStatus);

			lastGearShift = System.currentTimeMillis();
			lastPolarShift = System.currentTimeMillis();

			NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(1);
			NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(0);

		} catch (Exception e) {
			System.out.print(e);
		}
	}

	@Override
	public void teleopUpdate(LogitechF310 driver, LogitechF310 operator) {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(1);
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1);

		if (driver.getRawButton(LogitechButton.BACK)) {
			shiftGear();
		}

		if (driver.getRawButton(LogitechButton.START)) {
			shiftPolar();
		}

		if (driver.getPOV() == 90) {
			setLeft(.50);
			setRight(.50);
		} else if (Math.abs(driver.getAxis(LogitechAxis.LY)) > .05 || Math.abs(driver.getAxis(LogitechAxis.RY)) > .05) {
			if (midLeft.getInverted()) {
				setLeft(-driver.getAxis(LogitechAxis.RY));
				setRight(-driver.getAxis(LogitechAxis.LY));
			} else {
				setLeft(-driver.getAxis(LogitechAxis.LY));
				setRight(-driver.getAxis(LogitechAxis.RY));
			}
		} else {
			setLeft(0);
			setRight(0);
		}

	}

	public void setLeft(double speed) {
		frontLeft.set(currentControl, speed);
		midLeft.set(currentControl, speed);
		backLeft.set(currentControl, speed);
	}

	public void setRight(double speed) {
		frontRight.set(currentControl, -speed);
		midRight.set(currentControl, -speed);
		backRight.set(currentControl, -speed);
	}

	public void shiftGear() {
		if (System.currentTimeMillis() - lastGearShift > 1500) {
			transmission_1.set(!transmission_1.get());
			transmission_2.set(!transmission_2.get());
			lastGearShift = System.currentTimeMillis();
		}
	}

	public void shiftPolar() {
		if (System.currentTimeMillis() - lastPolarShift > 1500) {
			frontLeft.setInverted(!frontLeft.getInverted());
			midLeft.setInverted(!midLeft.getInverted());
			backLeft.setInverted(!backLeft.getInverted());
			frontRight.setInverted(!frontRight.getInverted());
			midRight.setInverted(!midRight.getInverted());
			backRight.setInverted(!backRight.getInverted());
			lastPolarShift = System.currentTimeMillis();
		}
	}

	public void stop() {
		setLeft(0);
		setRight(0);
	}

	public void update() {

	}

}
