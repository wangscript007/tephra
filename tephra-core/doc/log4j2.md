# log4j2.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="info">
    <Properties>
        <property name="pattern">%-5level %d{yyyy-MM-dd HH:mm:ss} %msg%n</property>
        <property name="size">50 MB</property>
    </Properties>
    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <PatternLayout pattern="${pattern}"/>
            <Filters>
                <RegexFilter regex=".*org.lpw.tephra.scheduler.SecondsSchedulerImpl.*" onMatch="DENY" onMismatch="ACCEPT"/>
            </Filters>
        </Console>
        <RollingFile name="file.debug" fileName="logs/tephra.debug.log" filePattern="logs/tephra.debug.%i.log.gz">
            <PatternLayout pattern="${pattern}"/>
            <SizeBasedTriggeringPolicy size="${size}"/>
            <Filters>
                <ThresholdFilter level="info" onMatch="DENY" onMismatch="NEUTRAL"/>
                <ThresholdFilter level="debug" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
        </RollingFile>
        <RollingFile name="file.info" fileName="logs/tephra.info.log" filePattern="logs/tephra.info.%i.log.gz">
            <PatternLayout pattern="${pattern}"/>
            <SizeBasedTriggeringPolicy size="${size}"/>
            <Filters>
                <ThresholdFilter level="warn" onMatch="DENY" onMismatch="NEUTRAL"/>
                <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
        </RollingFile>
        <RollingFile name="file.warn" fileName="logs/tephra.warn.log" filePattern="logs/tephra.warn.%i.log.gz">
            <PatternLayout pattern="${pattern}"/>
            <SizeBasedTriggeringPolicy size="${size}"/>
            <Filters>
                <ThresholdFilter level="error" onMatch="DENY" onMismatch="NEUTRAL"/>
                <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
        </RollingFile>
        <RollingFile name="file.error" fileName="logs/tephra.error.log" filePattern="logs/tephra.error.%i.log.gz">
            <PatternLayout pattern="${pattern}"/>
            <SizeBasedTriggeringPolicy size="${size}"/>
            <Filters>
                <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="console"/>
        </Root>
        <Logger name="tephra.util.logger" level="info">
            <appender-ref ref="file.debug"/>
            <appender-ref ref="file.info"/>
            <appender-ref ref="file.warn"/>
            <appender-ref ref="file.error"/>
        </Logger>
        <Logger name="org.mongodb" level="warn"/>
    </Loggers>
</Configuration>
```