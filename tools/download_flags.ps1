# Downloads flag PNGs (flagcdn.com) and generates assets/countries.json.
$ErrorActionPreference = 'Stop'
$assets = Join-Path $PSScriptRoot '..\app\src\main\assets'
$flagDir = Join-Path $assets 'flags'
New-Item -ItemType Directory -Force $flagDir | Out-Null

$continents = [ordered]@{
    'Africa'        = 'dz ao bj bw bf bi cv cm cf td km cg cd ci dj eg gq er sz et ga gm gh gn gw ke ls lr ly mg mw ml mr mu ma mz na ne ng rw st sn sc sl so za ss sd tz tg tn ug zm zw'
    'Asia'          = 'af am az bh bd bt bn kh cn cy ge in id ir iq il jp jo kz kw kg la lb my mv mn mm np kp om pk ph qa sa sg kr lk sy tj th tl tr tm ae uz vn ye ps tw'
    'Europe'        = 'al ad at by be ba bg hr cz dk ee fi fr de gr hu is ie it lv li lt lu mt md mc me nl mk no pl pt ro ru sm rs sk si es se ch ua gb va xk'
    'North America' = 'ag bs bb bz ca cr cu dm do sv gd gt ht hn jm mx ni pa kn lc vc tt us'
    'South America' = 'ar bo br cl co ec gy py pe sr uy ve'
    'Oceania'       = 'au fj ki mh fm nr nz pw pg ws sb to tv vu'
}

$names = Invoke-RestMethod 'https://flagcdn.com/en/codes.json'
$list = @()
foreach ($c in $continents.Keys) {
    foreach ($code in $continents[$c].Split(' ')) {
        $name = $names.$code
        if (-not $name) { throw "No name for $code" }
        $file = Join-Path $flagDir "$code.png"
        if (-not (Test-Path $file)) {
            Invoke-WebRequest "https://flagcdn.com/w640/$code.png" -OutFile $file
        }
        $list += [pscustomobject]@{ code = $code; name = $name; continent = $c }
    }
}
$list = $list | Sort-Object name
$json = ConvertTo-Json -InputObject @($list) -Depth 3
[IO.File]::WriteAllText((Join-Path $assets 'countries.json'), $json, (New-Object Text.UTF8Encoding($false)))
"Generated $($list.Count) countries"
