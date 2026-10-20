// Development environment configuration for local testing
// By default points to local Spring Boot (http://localhost:8080).
// To test against the deployed Render backend locally in development mode,
// run in browser console: localStorage.setItem('RH_USE_RENDER_API', 'true');
const LOCAL_BASE = 'http://localhost:8080';
const RENDER_BASE = 'https://royal-hotels.onrender.com';

const useRender = typeof window !== 'undefined' && localStorage.getItem('RH_USE_RENDER_API') === 'true';
const selectedBase = useRender ? RENDER_BASE : LOCAL_BASE;

export const environment = {
  production: false,
  apiBaseUrl: selectedBase,
  apiUrl: `${selectedBase}/api`
};
