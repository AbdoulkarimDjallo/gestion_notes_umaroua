@echo off
REM Script de compilation pour GestionNotesUMaroua
REM Université de Maroua - Faculté des Sciences

echo ============================================
echo Compilation du projet GestionNotesUMaroua
echo ============================================
echo.

REM Créer le dossier out s'il n'existe pas
if not exist "out" mkdir out

REM Compiler tous les fichiers Java avec les dépendances
javac -d out -cp "lib\*;src" -encoding UTF-8 ^
    src\*.java ^
    src\model\*.java ^
    src\controller\*.java ^
    src\database\*.java ^
    src\security\*.java ^
    src\view\*.java ^
    src\tests\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo Compilation reussie !
    echo ============================================
    echo.
    echo Pour executer l'application :
    echo java -cp "out;lib\*" Main
    echo.
) else (
    echo.
    echo ============================================
    echo Erreur lors de la compilation !
    echo ============================================
    exit /b 1
)
