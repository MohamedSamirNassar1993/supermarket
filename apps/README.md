# Supermarket ERP — Angular Micro-Frontends

Native Federation workspace for the Supermarket ERP migration.

## Structure

| App | Port | Role |
|-----|------|------|
| `shell` | 4200 | Host — layout, router, federation manifest |
| `mf-auth` | 4201 | Remote — login (`/login`) |
| `mf-catalog` | 4202 | Remote — catalog stub |
| `mf-inventory` | 4203 | Remote — inventory stub |
| `mf-procurement` | 4204 | Remote — procurement stub |
| `mf-sales` | 4205 | Remote — sales stub |
| `mf-operations` | 4206 | Remote — operations stub |
| `mf-analytics` | 4207 | Remote — analytics stub |
| `shared-ui` | — | Shared library (Auth, API, Branch context, Material) |

## Prerequisites

- Node.js **20 LTS** or **22+** (Angular 19 requires `^18.19.1 || ^20.11.1 || >=22.0.0`)
- Backend API on `http://localhost:8080` (start via `docker compose -f docker/docker-compose.micro.yml --profile micro up`)

## Install

Requires **Node 20 LTS** or **22+** (`nvm use 20`).

From the workspace root:

```powershell
cd c:\projects\Supermarket\apps

# Remove stale per-app installs (from prior scaffold) and root node_modules
Get-ChildItem -Directory -Recurse -Filter node_modules |
  Where-Object { $_.FullName -notmatch '\\apps\\node_modules$' } |
  Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force node_modules -ErrorAction SilentlyContinue
Get-ChildItem -Recurse -Filter package-lock.json | Remove-Item -Force

npm install
```

Close any running `ng serve` or IDE file watchers if you see `EPERM` / `ENOTEMPTY` errors during install.

## Run locally

Start remotes first, then the shell (each in its own terminal):

```bash
npm run start:auth
npm run start:catalog
# ... other remotes as needed

npm run start:shell
```

Or from individual app folders:

```bash
cd shell && npm start          # http://localhost:4200
cd mf-auth && npm start        # http://localhost:4201
```

The shell proxies `/api` → `http://localhost:8080` via `shell/proxy.conf.json`.

## Routes

| Path | Remote |
|------|--------|
| `/login` | mf-auth |
| `/catalog` | mf-catalog |
| `/inventory` | mf-inventory |
| `/procurement` | mf-procurement |
| `/sales` | mf-sales |
| `/operations` | mf-operations |
| `/analytics` | mf-analytics |

## Shared library

Import from `@supermarket/shared-ui`:

- `AuthService`, `ApiService`, `BranchContextService`
- `authInterceptor`, `MATERIAL_IMPORTS`, `TranslatePipe`
- `API_BASE_URL` token (defaults to `/api/v1`)
