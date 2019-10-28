/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import frc.util.LogitechF310;

public class Robot extends TimedRobot {
  // region Objects
  LogitechF310 driver, operator;
  Drive drive;
  Manipulator manipulator;
  Arm arm;
  Elevator elevator;
  PushUp pushUp;
  Gyro gyro;
  // endregion
  // region Variables
  int step = 0;

  // endregion
  @Override
  public void robotInit() {
    driver = new LogitechF310(0);
    operator = new LogitechF310(1);
    drive = new Drive();
    manipulator = new Manipulator();
    arm = new Arm();
    elevator = new Elevator(arm);
    pushUp = new PushUp();
    gyro = new Gyro();

  }

  @Override
  public void robotPeriodic() {
    gyro.update();
    arm.update();
    elevator.update();
    manipulator.update();
    pushUp.update();

  }

  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {

    drive.teleopUpdate(driver, operator);
    manipulator.sandstorm(driver, operator);
    arm.teleopUpdate(driver, operator);
    elevator.teleopUpdate(driver, operator);
    pushUp.teleopUpdate(driver, operator);
  }

  @Override
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {
    drive.teleopUpdate(driver, operator);
    manipulator.teleopUpdate(driver, operator);
    arm.teleopUpdate(driver, operator);
    elevator.teleopUpdate(driver, operator);
    pushUp.teleopUpdate(driver, operator);

  }

  @Override
  public void testPeriodic() {

  }
}
