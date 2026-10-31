#!/usr/bin/env bash
# Smoke test del despliegue: verifica salud, autenticación y un flujo funcional completo
# (región -> comuna -> coordenada -> GeoJSON) a través del gateway.
#
# Uso: scripts/smoke-test.sh [BASE_URL]      (por defecto http://localhost:8082)
# Requiere: curl, openssl. Variable opcional JWT_SECRET (Base64) igual a la del despliegue.
set -euo pipefail

BASE_URL="${1:-http://localhost:8082}"
JWT_SECRET="${JWT_SECRET:-c2Fub3N5c2Fsdm9zLXNlY3JldC1rZXktcGFyYS1qd3QtMjAyNg==}"

b64url() { openssl base64 -A | tr '+/' '-_' | tr -d '='; }

make_jwt() {
  local header payload sig hexkey exp
  exp=$(( $(date +%s) + 300 ))
  header=$(printf '{"alg":"HS256","typ":"JWT"}' | b64url)
  payload=$(printf '{"sub":"smoke-test","exp":%s}' "$exp" | b64url)
  hexkey=$(printf '%s' "$JWT_SECRET" | base64 -d | od -An -vtx1 | tr -d ' \n')
  sig=$(printf '%s.%s' "$header" "$payload" | openssl dgst -sha256 -mac HMAC -macopt "hexkey:$hexkey" -binary | b64url)
  printf '%s.%s.%s' "$header" "$payload" "$sig"
}

fail() { echo "FALLO: $*" >&2; exit 1; }

# $1=metodo $2=ruta $3=codigo esperado [$4=body]  -> imprime el cuerpo de la respuesta
call() {
  local method="$1" path="$2" expected="$3" body="${4:-}" out code
  out=$(mktemp)
  if [ -n "$body" ]; then
    code=$(curl -s -o "$out" -w '%{http_code}' -X "$method" "$BASE_URL$path" \
      -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d "$body")
  else
    code=$(curl -s -o "$out" -w '%{http_code}' -X "$method" "$BASE_URL$path" -H "Authorization: Bearer $TOKEN")
  fi
  [ "$code" = "$expected" ] || { cat "$out" >&2; fail "$method $path -> HTTP $code (esperado $expected)"; }
  cat "$out"; rm -f "$out"
}

TOKEN="$(make_jwt)"

echo "1/6 Health público";            curl -sf "$BASE_URL/actuator/health" | grep -q '"status":"UP"' || fail "health no está UP"
echo "2/6 Sin token => 403";          [ "$(curl -s -o /dev/null -w '%{http_code}' "$BASE_URL/api/regiones")" = "403" ] || fail "endpoint protegido accesible sin token"
echo "3/6 Token inválido => 401";     [ "$(curl -s -o /dev/null -w '%{http_code}' "$BASE_URL/api/regiones" -H 'Authorization: Bearer invalido')" = "401" ] || fail "token inválido no rechazado"

echo "4/6 Crear región y comuna"
REGION_ID=$(call POST /api/regiones 201 '{"nombreRegion":"Region Smoke"}' | grep -o '"idRegion":[0-9]*' | cut -d: -f2)
COMUNA_ID=$(call POST /api/comunas 201 "{\"nombreComuna\":\"Comuna Smoke\",\"idRegion\":$REGION_ID}" | grep -o '"idComuna":[0-9]*' | cut -d: -f2)

echo "5/6 Crear coordenada y validar datos inválidos"
call POST /api/coordenadas 201 "{\"ubicacionLat\":-33.045,\"ubicacionLon\":-71.61,\"idReporte\":9001,\"idComuna\":$COMUNA_ID,\"direccion\":\"Smoke 123\"}" >/dev/null
call POST /api/coordenadas 400 '{}' >/dev/null

echo "6/6 Consultar GeoJSON"
call GET "/api/geo/coordenadas/comuna/$COMUNA_ID" 200 | grep -q '"FeatureCollection"' || fail "GeoJSON sin FeatureCollection"

echo "OK: smoke test superado contra $BASE_URL"
