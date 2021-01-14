## Oribee

A simple tool for funny bees to collect IMU-sensors.

### data columns
All data are stored in a single csv file.

The structure of the data shown below:

time[0], accelerometer[1-3], gyroscope[4-6], game rotation vector[7-10], rotation vector[11-14], gyroscope uncalibrated[15-17]， orientation[18-20]

|字段|位置|说明|
|:-----|:-----|:-----|
|time|0|时间戳|
|TYPE_ACCELEROMETER|1-3|加速度|
|TYPE_GYROSCOPE|4-6|陀螺仪|
|TYPE_GAME_ROTATION_VECTOR|7-10|未经过磁场矫正的旋转向量|
|TYPE_ROTATION_VECTOR|11-14|经过磁场矫正的旋转向量|
|TYPE_GYROSCOPE_UNCALIBRATED|15-17|未经过矫正的陀螺仪|
|TYPE_ORIENTATION|18-20|设备的位置|
|TYPE_MAGNETIC_FIELD|18-20|磁场|
|TYPE_GRAVITY|18-20|重力|
|TYPE_LINEAR_ACCELERATION|18-20|线性加速度|


