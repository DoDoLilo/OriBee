## Oribee

A simple tool for funny bees to collect IMU-sensors.

### data columns
All data are stored in a single csv file.

The structure of the data shown below:

|字段|位置|说明|
|:-----|:-----|:-----|
|time|0|时间戳|
|TYPE_ACCELEROMETER|1-3|加速度|
|TYPE_GYROSCOPE|4-6|陀螺仪|
|TYPE_GAME_ROTATION_VECTOR|7-10|未经过磁场矫正的旋转向量|
|TYPE_ROTATION_VECTOR|11-14|经过磁场矫正的旋转向量|
|TYPE_GYROSCOPE_UNCALIBRATED|15-17|未经过矫正的陀螺仪|
|TYPE_ORIENTATION|18-20|设备的位置|
|TYPE_MAGNETIC_FIELD|21-23|磁场|
|TYPE_GRAVITY|24-26|重力|
|TYPE_LINEAR_ACCELERATION|27-29|线性加速度|


