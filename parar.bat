@echo off
title AlugaFacil - Encerrando...
color 0C

echo ============================================
echo       AlugaFacil - Encerrando servicos
echo ============================================
echo.

echo [INFO] Parando containers do banco de dados...
docker compose down

if errorlevel 1 (
    echo [AVISO] Falha ao parar os containers. Verifique manualmente com: docker compose ps
) else (
    echo [OK] Banco de dados encerrado.
)

echo.
echo Servicos encerrados. Feche as janelas do servidor e do cliente manualmente.
echo.
pause
