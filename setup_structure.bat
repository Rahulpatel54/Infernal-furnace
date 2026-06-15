@echo off
echo ============================================
echo  Infernal Furnace Mod - Folder Setup
echo ============================================

:: Root
mkdir infernal_furnace

:: Gradle wrapper
mkdir infernal_furnace\gradle\wrapper

:: Java source
mkdir infernal_furnace\src\main\java\com\yourname\infernal_furnace
mkdir infernal_furnace\src\main\java\com\yourname\infernal_furnace\block
mkdir infernal_furnace\src\main\java\com\yourname\infernal_furnace\block\entity

:: Resources - assets
mkdir infernal_furnace\src\main\resources\assets\infernal_furnace\blockstates
mkdir infernal_furnace\src\main\resources\assets\infernal_furnace\lang
mkdir infernal_furnace\src\main\resources\assets\infernal_furnace\models\block
mkdir infernal_furnace\src\main\resources\assets\infernal_furnace\models\item
mkdir infernal_furnace\src\main\resources\assets\infernal_furnace\textures\block

:: Resources - data
mkdir infernal_furnace\src\main\resources\data\infernal_furnace\recipes
mkdir infernal_furnace\src\main\resources\data\infernal_furnace\tags\blocks\mineable

echo.
echo Folder structure created!
echo.
echo ============================================
echo  NEXT STEPS:
echo ============================================
echo  1. Open the infernal_furnace\ folder
echo  2. Paste each file from the guide into its path
echo  3. Copy the 5 PNG textures from the resource pack into:
echo     src\main\resources\assets\infernal_furnace\textures\block\
echo  4. Run: gradlew genSources
echo  5. Run: gradlew build
echo ============================================
pause
