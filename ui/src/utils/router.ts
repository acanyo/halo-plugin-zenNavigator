import { inject } from "vue";

export interface Router {
  push: (path: string) => void;
  replace: (path: string) => void;
  go: (delta: number) => void;
  back: () => void;
  forward: () => void;
}

export interface Route {
  path: string;
  params: Record<string, string>;
  query: Record<string, string>;
  hash: string;
  fullPath: string;
  matched: any[];
  name: string | null | undefined;
  meta: Record<string, any>;
}

export function useRouter(): Router {
  const router = inject<Router>("router");
  if (!router) {
    return {
      push: (path: string) => {
        window.location.href = path;
      },
      replace: (path: string) => {
        window.location.replace(path);
      },
      go: (delta: number) => {
        window.history.go(delta);
      },
      back: () => {
        window.history.back();
      },
      forward: () => {
        window.history.forward();
      },
    };
  }
  return router;
}

export function useRoute(): Route {
  const route = inject<Route>("route");
  if (!route) {
    return {
      path: window.location.pathname,
      params: {},
      query: Object.fromEntries(new URLSearchParams(window.location.search)),
      hash: window.location.hash,
      fullPath: window.location.pathname + window.location.search + window.location.hash,
      matched: [],
      name: undefined,
      meta: {},
    };
  }
  return route;
} 