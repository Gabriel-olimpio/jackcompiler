param(
    [Parameter(Mandatory = $true)]
    [string]$PastaProject11
)

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot
$origem = (Resolve-Path $PastaProject11).Path
$classes = Join-Path $raiz "target\classes"
$destino = Join-Path $raiz "target\project11"
$programas = @("Seven", "ConvertToBin", "Square", "Average", "Pong", "ComplexArrays")

Push-Location $raiz
try {
    mvn -q -DskipTests compile
    if ($LASTEXITCODE -ne 0) { throw "Falha ao compilar o projeto com Maven." }
} finally {
    Pop-Location
}

foreach ($programa in $programas) {
    $pastaOrigem = Join-Path $origem $programa
    if (-not (Test-Path $pastaOrigem -PathType Container)) {
        throw "Pasta oficial nao encontrada: $pastaOrigem"
    }

    $jack = @(Get-ChildItem -Path $pastaOrigem -Filter "*.jack" -File)
    if ($jack.Count -eq 0) { throw "Nenhum .jack em $pastaOrigem" }

    $pastaDestino = Join-Path $destino $programa
    New-Item -ItemType Directory -Force -Path $pastaDestino | Out-Null
    Get-ChildItem -Path $pastaDestino -File | Remove-Item -Force
    Copy-Item -Path (Join-Path $pastaOrigem "*.jack") -Destination $pastaDestino

    & java -cp $classes Main $pastaDestino
    if ($LASTEXITCODE -ne 0) { throw "Falha ao compilar: $programa" }

    $vm = @(Get-ChildItem -Path $pastaDestino -Filter "*.vm" -File)
    if ($vm.Count -ne $jack.Count) {
        throw "Quantidade incorreta de VM em $programa. Jack: $($jack.Count); VM: $($vm.Count)"
    }

    Write-Host "OK - $programa : $($jack.Count) arquivo(s) .jack compilado(s)" -ForegroundColor Green
}

Write-Host ""
Write-Host "Os arquivos compilados estao em: $destino"
Write-Host "ATENCAO: a geracao de VM foi verificada; a execucao no VM Emulator ainda e necessaria."
