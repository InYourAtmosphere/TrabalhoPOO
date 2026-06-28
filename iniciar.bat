@echo off
setlocal EnableDelayedExpansion
title AlugaFacil - Iniciando...
color 0A

echo ============================================
echo       AlugaFacil - Sistema de Aluguel
echo ============================================
echo.

:: ---- Verificar pre-requisitos ----

where java >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java nao encontrado. Instale o JDK 17+ e adicione ao PATH.
    pause
    exit /b 1
)

where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Maven nao encontrado. Instale o Maven e adicione ao PATH.
    echo        https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [OK] Pre-requisitos encontrados.
echo.

:: ---- Iniciar o servidor em uma nova janela ----

echo [1/2] Iniciando o servidor (Spring Boot na porta 8081)...
start "AlugaFacil - Servidor" cmd /k "cd /d "%~dp0server" && mvn spring-boot:run -DskipTests"

:: Aguardar o servidor responder na porta 8081
echo [INFO] Aguardando o servidor iniciar...
set /a tentativas=0
:aguardar_server
set /a tentativas+=1
if %tentativas% gtr 60 (
    echo [ERRO] Servidor nao respondeu na porta 8081 apos 120 segundos.
    echo        Verifique a janela do servidor para detalhes do erro.
    pause
    exit /b 1
)
powershell -Command "try { (New-Object Net.Sockets.TcpClient('localhost', 8081)).Close(); exit 0 } catch { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo|set /p="."
    timeout /t 2 /nobreak >nul
    goto aguardar_server
)
echo.
echo [OK] Servidor rodando em http://localhost:8081
echo.

:: ---- Iniciar o cliente ----

echo [2/2] Iniciando o cliente (interface grafica)...
start "AlugaFacil - Cliente" cmd /k "cd /d "%~dp0client" && mvn compile exec:java"

echo.
echo ============================================
echo  Sistema iniciado com sucesso!
echo.
echo  Servidor: http://localhost:8081
echo  Swagger:  http://localhost:8081/swagger-ui.html
echo  Banco:    alugafacil.db (SQLite, na pasta server)
echo.
echo  Para encerrar: feche as janelas do servidor
echo  e do cliente.
echo ============================================
echo.
pause
