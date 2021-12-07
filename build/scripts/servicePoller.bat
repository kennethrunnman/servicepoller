@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  servicePoller startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and SERVICE_POLLER_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\servicePoller-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\vertx-web-4.2.1.jar;%APP_HOME%\lib\vertx-jdbc-client-4.2.1.jar;%APP_HOME%\lib\vertx-web-client-4.2.1.jar;%APP_HOME%\lib\vertx-infinispan-4.2.1.jar;%APP_HOME%\lib\vertx-mysql-client-4.2.1.jar;%APP_HOME%\lib\vertx-config-4.2.1.jar;%APP_HOME%\lib\vertx-sql-client-4.2.1.jar;%APP_HOME%\lib\vertx-web-common-4.2.1.jar;%APP_HOME%\lib\vertx-auth-common-4.2.1.jar;%APP_HOME%\lib\vertx-bridge-common-4.2.1.jar;%APP_HOME%\lib\vertx-core-4.2.1.jar;%APP_HOME%\lib\rxjava-3.1.3.jar;%APP_HOME%\lib\mysql-connector-java-8.0.27.jar;%APP_HOME%\lib\flyway-core-8.0.2.jar;%APP_HOME%\lib\gradle-docker-1.2.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.69.Final.jar;%APP_HOME%\lib\netty-codec-http2-4.1.69.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.69.Final.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.69.Final.jar;%APP_HOME%\lib\netty-handler-4.1.69.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.69.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.69.Final.jar;%APP_HOME%\lib\netty-codec-4.1.69.Final.jar;%APP_HOME%\lib\netty-transport-4.1.69.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.69.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.69.Final.jar;%APP_HOME%\lib\netty-common-4.1.69.Final.jar;%APP_HOME%\lib\docker-java-0.9.0.jar;%APP_HOME%\lib\jackson-jaxrs-json-provider-2.3.3.jar;%APP_HOME%\lib\jackson-jaxrs-base-2.3.3.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.3.3.jar;%APP_HOME%\lib\jackson-databind-2.11.4.jar;%APP_HOME%\lib\jackson-core-2.11.4.jar;%APP_HOME%\lib\jackson-annotations-2.11.4.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\protobuf-java-3.11.4.jar;%APP_HOME%\lib\guava-17.0.jar;%APP_HOME%\lib\c3p0-0.9.5.5.jar;%APP_HOME%\lib\infinispan-multimap-12.1.7.Final.jar;%APP_HOME%\lib\infinispan-clustered-lock-12.1.7.Final.jar;%APP_HOME%\lib\infinispan-clustered-counter-12.1.7.Final.jar;%APP_HOME%\lib\infinispan-core-12.1.7.Final.jar;%APP_HOME%\lib\wildfly-common-1.5.1.Final.jar;%APP_HOME%\lib\caffeine-2.8.4.jar;%APP_HOME%\lib\jersey-apache-client4-1.9.jar;%APP_HOME%\lib\jersey-client-1.18.jar;%APP_HOME%\lib\jersey-multipart-1.18.jar;%APP_HOME%\lib\jersey-core-1.18.jar;%APP_HOME%\lib\httpclient-4.2.5.jar;%APP_HOME%\lib\commons-compress-1.5.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\commons-io-2.3.jar;%APP_HOME%\lib\jnr-unixsocket-0.3.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.5.jar;%APP_HOME%\lib\slf4j-api-1.7.5.jar;%APP_HOME%\lib\mchange-commons-java-0.2.19.jar;%APP_HOME%\lib\infinispan-commons-12.1.7.Final.jar;%APP_HOME%\lib\protostream-types-4.4.1.Final.jar;%APP_HOME%\lib\protostream-4.4.1.Final.jar;%APP_HOME%\lib\jgroups-4.2.12.Final.jar;%APP_HOME%\lib\jboss-transaction-api_1.2_spec-1.1.1.Final.jar;%APP_HOME%\lib\jboss-threads-2.3.3.Final.jar;%APP_HOME%\lib\jboss-logging-3.4.1.Final.jar;%APP_HOME%\lib\mimepull-1.9.3.jar;%APP_HOME%\lib\httpcore-4.2.4.jar;%APP_HOME%\lib\commons-logging-1.1.1.jar;%APP_HOME%\lib\commons-codec-1.6.jar;%APP_HOME%\lib\xz-1.2.jar;%APP_HOME%\lib\jnr-enxio-0.4.jar;%APP_HOME%\lib\jnr-ffi-1.0.3.jar;%APP_HOME%\lib\jnr-constants-0.8.4.jar;%APP_HOME%\lib\jffi-1.2.23.jar;%APP_HOME%\lib\jffi-1.2.23-native.jar;%APP_HOME%\lib\asm-commons-4.0.jar;%APP_HOME%\lib\asm-analysis-4.0.jar;%APP_HOME%\lib\asm-util-4.0.jar;%APP_HOME%\lib\asm-tree-4.0.jar;%APP_HOME%\lib\asm-4.0.jar;%APP_HOME%\lib\jnr-x86asm-1.0.2.jar


@rem Execute servicePoller
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %SERVICE_POLLER_OPTS%  -classpath "%CLASSPATH%" io.vertx.core.Launcher %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable SERVICE_POLLER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%SERVICE_POLLER_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
