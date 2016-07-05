# EBADS
Entropy Based Anomaly Detection for PCF TTEthernet Protocol

## Abstract
EBADS focuses on the security of the clock synchronization algorithm in TTEthernet, which uses Protocol Control Frames (PCF) for its implementation. We have defined two attack scenarios, namely 'manipulation attack' and 'delay attack', which can break the clock synchronization, and thus lead to the failure of the highly-critical Time-Triggered applicatoins.

We propose a monitoring system for clock synchronization that is aimed at determining if the system is under a security attack. The attack is detected via an 'anomaly detection' method, which in our case is based on 'entropy' measures, a concept from information theory.

Our anomaly detection works in three steps: (1) features selection, (2) feature quantization, and (3) comparison. (1) The experiment selects packet 'timestamp' and 'pcf_transparent_clock' header of a PCF as the features. (2) Probability Mass Function (PMF) as component to count entropy represents the feature quantization. (3) Relative entropy is used to compare the incoming communication flow to the baseline communication flow (i.e., not under attack).

The proposed solution is able to detect anomaly caused by delay attack to the sensitivity of 3 microseconds and anomaly caused by manipulation attack by 10 nanoseconds.

## Models
![EBADS work flow](https://drive.google.com/uc?export=view&id=0B6B8Zyf5xqZnLVlXb1d5UjZEU2s)

1. Baseline input is fed into the system through a mapper interface.
2. The mapper extracts the features out of the baseline input and constructs a map representing the entropy.
3. Input is fed into the system through a reader interface.
4. The reader retrieves the entropy map from the mapper.
5. The reader compare the entropy of the input with the baseline and provides the relative entropy value.

## Assumptions
* We assume there are no other traffic in the simulation
* We assume all the traffics are generated in the same time
* We assume there is an internal man-in-the-middle attacker

## Input files
Input files are located in ebads-core/input
* Normal
  * normal/normal.pcap = baseline file 
* Manipulated
  * manipulation/<xy>secondmanipulation.pcap = manipulation attack with manipulation of x ysecond, y = micro/nano/pico.
  * manipulation/manipulation<in/out>schedule.pcap = manipulation attack that fall in or out of the schedule after the attack.
* Delayed
  * delayed/<xy>seconddelay.pcap = delay attack with delay of x ysecond, y = micro/nano/pico.
* Intercepted
  * interception/interception<short/long>.pcap = intercept for short or long period of time.

## Output files
![EBADS output file](https://drive.google.com/uc?export=view&id=0B6B8Zyf5xqZncjhzVWI2cVNXaUk)

Output files are containing graphic with x axis as the amount of millisecond from the beginning of the trace while y axis showing the amount of the Relative Entropy.

## Configuration files
The ebads-core can be run with following command: 
```
java -jar ebads-core-1.0-SNAPSHOT.jar <mapper-name> <reader-name> <baseline-folder> <input-folder>
```

## The algorithms developed and used
* Probability Mass Function is used as feature quantization.
* To spread the quantization into a distribution, normal distribution is used
* To compare baseline distribution with input distribution, relative entropy is used.

Relative entropy formula is as follows:

![Relative Entropy Formula](https://drive.google.com/uc?export=view&id=0B6B8Zyf5xqZnMFhmWmdOck5FY1E)

## Limitation of your project
* When using timestamp and transparent clock feature together, the result is skewed towards transparent clock due to different units.

## Future work
* Install the implementation in real TTEthernet system.
* Find other relevant features for manipulation and delay attack.
* Extends the solution for other attacks by extending mapper and reader.
* Implements other method such as Neural Network, Shared Nearest Neighbor, and Kernel Method.
* Perfecting the use of multiple features.

## Building the application
Before running the application ensure your system:
1. Has minimum JRE 7
2. Not using port 8080

### Building ebads-core:

Build gradle wrapper for ebads-core
```
cd ebads-core
gradle wrapper
```
Build ebads-core
```
cd ebads-core
./gradlew build
```

### Building ebads-ui:

Build gradle wrapper for ebads-ui
```
cd ebads-ui
gradle wrapper
```
Build ebads-core
```
cd ebads-ui
./gradlew build
```

### To run the application in Linux:
```
cd ebads-ui
./gradlew bootRun
```

Use similar gradle application to run the application in Windows or Mac (untested)
