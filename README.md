# ltl-verifier

## Usage
### Packaging
`mvn package`

It produces jar file: /target/verifier-1.0-SNAPSHOT-jar-with-dependencies.jar

### Jar
Download: [verifier.jar](https://github.com/mikita95/ltl-verifier/releases/download/1.1/verifier.jar)

### Using jar
`java -jar verifier.jar`

```
-f,--formula <arg>   ltl-formula
-l,--file <arg>      ltl-file path
-x,--xml <arg>       xml file path
```

## Output example
```java -jar verifier.jar --xml=src/main/resources/diagram1.xml --file=src/main/resources/test1.txt```
```
Formula: F hal_init
it is correct

Formula: G ! F(PRESTART)
it is correct

Formula: G (PRESTART -> (PRESTART U POWER_ON))
it is correct

Formula: G ((POWER_ON & CHG) -> (FLASH U POWER_ON))
it is correct

Formula: G (hal_init -> (F tim4_enable))
it is correct

Formula: G (F PRESTART)
it is not correct
path:
Start
Start, tick
Start, tick, hal_init
Start, tick, tim4_enable
PRESTART
PRESTART, tick
PRESTART, tick, shell_deinit
PRESTART, tick, bq_deinit
PRESTART, tick, pin_reset_s1
PRESTART, tick, pin_reset_s2
PRESTART, tick, pin_reset_s3
PRESTART, tick, delay_5000
POWER_ON
POWER_ON, TIMER
SLEEP
SLEEP, INTERRUPT
POWER_ON
POWER_ON, TIMER
 cycle:
POWER_ON
SLEEP, INTERRUPT
SLEEP
POWER_ON, TIMER

Formula: G (SLEEP -> (F POWER_ON))
it is correct

Formula: G (hal_init -> (X tim4_enable))
it is correct

Formula: G (pin_reset_s1 -> (X pin_reset_s2))
it is correct

Formula: G (pin_reset_s2 -> (X pin_reset_s3))
it is correct
```
