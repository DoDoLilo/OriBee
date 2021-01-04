## Oribee

A simple tool for funny bees to collect IMU-sensors.

### data columns
All data are stored in a single csv file.

The structure of the data shown below:

time[0], accelerometer[1-3], gyroscope[4-6], game rotation vector[7-10], rotation vector[11-14], gyroscope uncalibrated[15-17]

|字段|位置|说明|
|:-----|:-----|:-----|
|time|0|时间戳|
|accelerometer|1-3|加速度|
|gyroscope|4-6|陀螺仪|
|game rotation vector|7-10|未经过磁场矫正的旋转向量|
|rotation vector|11-14|经过磁场矫正的旋转向量|
|gyroscope uncalibrated|15-17|未经过矫正的陀螺仪|


