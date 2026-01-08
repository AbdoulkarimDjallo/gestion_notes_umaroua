@echo off
REM Script complet pour creer la base de donnees et les tables
REM Universite de Maroua - Faculte des Sciences

echo ============================================
echo Creation complete de la base de donnees
echo ============================================
echo.

REM Parametres de connexion
set PGUSER=postgres
set PGPASSWORD=5596
set PGHOST=localhost
set PGPORT=5432
set DBNAME=gestion_notes_umaroua

echo Etape 1/2: Creation de la base de donnees...
echo.

REM Creer la base de donnees (sans transaction)
psql -U %PGUSER% -h %PGHOST% -p %PGPORT% -f 01_creer_base_donnees.sql

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR lors de la creation de la base de donnees!
    echo Verifiez que PostgreSQL est demarre et que les parametres sont corrects.
    pause
    exit /b 1
)

echo.
echo Etape 2/2: Creation des tables et insertion des donnees...
echo.

REM Creer les tables (dans la base creee)
psql -U %PGUSER% -h %PGHOST% -p %PGPORT% -d %DBNAME% -f 02_creer_tables.sql

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR lors de la creation des tables!
    echo La base de donnees existe mais les tables peuvent etre incompletes.
    pause
    exit /b 1
)

echo.
echo ============================================
echo Base de donnees creee avec succes!
echo ============================================
echo.
echo La base de donnees '%DBNAME%' est maintenant disponible.
echo Vous pouvez lancer l'application avec: run.bat
echo.
pause
