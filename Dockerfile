FROM centos

RUN yum update -y
RUN yum install -y wget

# install jdk-8u131
RUN cd /tmp \
    && wget --no-check-certificate --no-cookies --header 'Cookie: oraclelicense=accept-securebackup-cookie' http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.rpm \
    && yum install -y jdk-8u131-linux-x64.rpm \
    && rm -rf jdk-8u131-linux-x64.rpm

ENV JAVA_HOME /usr/java/latest
ENV PATH $PATH:$JAVA_HOME/bin

# install tomcat-9M21
RUN cd /usr/java \
    && wget http://mirror.bit.edu.cn/apache/tomcat/tomcat-9/v9.0.0.M21/bin/apache-tomcat-9.0.0.M21.tar.gz \
    && tar -zxf apache-tomcat-9.0.0.M21.tar.gz \
    && rm -rf apache-tomcat-9.0.0.M21.tar.gz \
    && cd apache-tomcat-9.0.0.M21 \
    && sed -i '/# OS specific support/i\JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"\n' bin/catalina.sh \
    && rm -rf webapps/*

ENV CATALINA_HOME /usr/java/apache-tomcat-9.0.0.M21

# service on boot
ENTRYPOINT $CATALINA_HOME/bin/catalina.sh run
