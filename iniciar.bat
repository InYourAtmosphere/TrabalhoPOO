@echo off
setlocal EnableDelayedExpansion
title AlugaFacil - Iniciando...
color 0A

echo ============================================
echo       AlugaFacil - Sistema de Aluguel
echo ============================================
echo.

:: ---- Verificar pre-requisitos ----

where docker >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker nao encontrado. Instale o Docker Desktop e tente novamente.
    echo        https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

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

:: ---- Verificar se Docker Desktop esta rodando ----

docker info >nul 2>&1
if errorlevel 1 (
    echo [INFO] Docker nao esta rodando. Abra o Docker Desktop e pressione qualquer tecla para continuar.
    pause >nul
    docker info >nul 2>&1
    if errorlevel 1 (
        echo [ERRO] Docker ainda nao esta acessivel.
        pause
        exit /b 1
    )
)

echo [OK] Docker esta rodando.
echo.

:: ---- Subir banco e servidor via Docker ----

echo [1/2] Subindo banco de dados e servidor (pode demorar na primeira vez)...
docker compose up -d --build db server
if errorlevel 1 (
    echo [ERRO] Falha ao subir os containers.
    pause
    exit /b 1
)

:: Aguardar o servidor responder na porta 8081
echo [INFO] Aguardando o servidor iniciar...
set /a tentativas=0
:aguardar_server
set /a tentativas+=1
if %tentativas% gtr 90 (
    echo [ERRO] Servidor nao respondeu na porta 8081 apos 3 minutos.
    echo        Verifique os logs com: docker compose logs server
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
echo  Banco:    localhost:5432 (trabalhopoo)
echo.
echo  Para encerrar: execute parar.bat
echo ============================================
echo.
pause
