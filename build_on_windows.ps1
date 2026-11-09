$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$version = '1.20.1-47.4.28'
$mdkUri = "https://maven.minecraftforge.net/net/minecraftforge/forge/$version/forge-$version-mdk.zip"
$expectedSha1 = 'eee9145e892bb5ca5fe72323bd81c8522c53417f'
$mdkZip = Join-Path $PSScriptRoot 'forge-mdk.zip'
$mdkFolder = Join-Path $PSScriptRoot '_official_mdk'
try {
    if (-not (Test-Path (Join-Path $PSScriptRoot 'gradlew.bat'))) {
        Write-Host 'Downloading official Forge MDK...'
        Invoke-WebRequest -Uri $mdkUri -OutFile $mdkZip -UseBasicParsing
        $sha1 = (Get-FileHash -Algorithm SHA1 -Path $mdkZip).Hash.ToLowerInvariant()
        if ($sha1 -ne $expectedSha1) { throw "MDK SHA1 mismatch: $sha1" }
        if (Test-Path $mdkFolder) { Remove-Item $mdkFolder -Recurse -Force }
        Expand-Archive -Path $mdkZip -DestinationPath $mdkFolder -Force
        Copy-Item (Join-Path $mdkFolder 'gradlew.bat') -Destination $PSScriptRoot -Force
        Copy-Item (Join-Path $mdkFolder 'gradlew') -Destination $PSScriptRoot -Force
        Copy-Item (Join-Path $mdkFolder 'gradle') -Destination $PSScriptRoot -Recurse -Force
    }
    Write-Host 'Starting official ForgeGradle build (requires JDK 17)...'
    $ErrorActionPreference = 'Continue'  # native process may print diagnostics on stderr
    & (Join-Path $PSScriptRoot 'gradlew.bat') --no-daemon build *> (Join-Path $PSScriptRoot 'build.log')
    $ErrorActionPreference = 'Stop'
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed, exit code $LASTEXITCODE. Check build.log" }
    $output = Join-Path $PSScriptRoot 'build\libs\HaloFriends-1.20.1-forge-0.5.0.jar'
    if (-not (Test-Path $output)) { throw "Build reported success but JAR was not found: $output" }
    New-Item -ItemType Directory -Force -Path (Join-Path $PSScriptRoot 'dist') | Out-Null
    Copy-Item $output -Destination (Join-Path $PSScriptRoot 'dist\HaloFriends-1.20.1-forge-0.5.0.jar') -Force
    Write-Host "Created: dist\HaloFriends-1.20.1-forge-0.5.0.jar"
    exit 0
} catch {
    $_ | Out-File -FilePath (Join-Path $PSScriptRoot 'build_errors.log') -Encoding utf8
    Write-Error $_
    exit 1
}
