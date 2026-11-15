$base = "http://localhost:8080/api/v1/products?category=%D8%A5%D9%84%D9%83%D8%AA%D8%B1%D9%88%D9%86%D9%8A%D8%A7%D8%AA"
# 1) أول طلب: لازم 200 + ترويسة ETag
$r1 = Invoke-WebRequest -Uri $base -UseBasicParsing
$etag = $r1.Headers["ETag"]
Write-Host "1) Status: $($r1.StatusCode)  ETag: $etag  Bytes: $($r1.RawContentLength)"

# 2) نفس الطلب مع If-None-Match: لازم 304 بدون جسم
try {
    $r2 = Invoke-WebRequest -Uri $base -Headers @{ "If-None-Match" = $etag } -UseBasicParsing
    Write-Host "2) Status: $($r2.StatusCode)  (متوقع 304)"
} catch {
    Write-Host "2) Status: $($_.Exception.Response.StatusCode.value__)  (متوقع 304)"
}