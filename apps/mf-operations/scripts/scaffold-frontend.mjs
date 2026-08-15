import { mkdirSync, writeFileSync, existsSync } from 'fs';
import { dirname, join } from 'path';

const root = join(import.meta.dirname, '..', 'src', 'app');

function ensureDir(p) {
  if (!existsSync(p)) mkdirSync(p, { recursive: true });
}

function write(relPath, content) {
  const full = join(root, relPath);
  ensureDir(dirname(full));
  writeFileSync(full, content, 'utf8');
  return full;
}

const files = {};

// ============ CORE MODELS ============
files['core/models/api-response.model.ts'] = `export interface FieldError {
  field: string;
  message: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  errorCode?: string;
  data: T;
  errors?: FieldError[];
  timestamp?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
`;

files['core/models/auth.model.ts'] = `export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserSummary;
}

export interface UserSummary {
  id: string;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  roles: string[];
  permissions: string[];
}
`;

files['core/models/branch.model.ts'] = `export interface Branch {
  id: string;
  name: string;
  code?: string;
  address?: string;
  active?: boolean;
  organizationId?: string;
}
`;

// ============ ENVIRONMENT ============
// written outside app folder

const written = [];
for (const [rel, content] of Object.entries(files)) {
  written.push(write(rel, content));
}

console.log('Scaffold partial complete:', written.length, 'files');
