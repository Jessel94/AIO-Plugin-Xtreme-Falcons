package com.useful.ucars;

public class SmoothMeta
{
  private volatile long lastTime;
  private volatile float speedFactor = 0.0F;
  private volatile float accFac = 1.0F;
  
  public SmoothMeta(float accFac)
  {
    this.lastTime = System.currentTimeMillis();
    this.speedFactor = 0.0F;
    this.accFac = accFac;
  }
  
  public float getFactor()
  {
    updateTime();
    incrementFactor();
    return this.speedFactor;
  }
  
  private float getA()
  {
    return (float)(0.025D * this.accFac);
  }
  
  private void incrementFactor()
  {
    if (this.speedFactor >= 0.97D)
    {
      this.speedFactor = 1.0F;
      return;
    }
    float diff = 1.0F - this.speedFactor;
    this.speedFactor += getA() * diff;
  }
  
  public void resetAcel()
  {
    this.speedFactor = 0.0F;
  }
  
  private void updateTime()
  {
    long now = System.currentTimeMillis();
    if (now - this.lastTime > 500L) {
      this.speedFactor = 0.0F;
    }
    this.lastTime = now;
  }
}
