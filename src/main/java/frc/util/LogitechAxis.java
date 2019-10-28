package frc.util;

public enum LogitechAxis {

  LX(0), LY(1), LT(2), RT(3), RX(4), RY(5);

  private int value;

  LogitechAxis(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

}
